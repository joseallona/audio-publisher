package com.audiopublisher.core.media

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaRecorder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import java.io.File
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

class MediaRecorderEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : RecorderEngine {

    private var audioRecord: AudioRecord? = null
    private var mediaCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null
    private var recordingThread: Thread? = null

    @Volatile private var isRecording = false
    @Volatile private var isPaused = false

    private var startTimeMs = 0L
    private var accumulatedMs = 0L
    private var outputPath = ""

    @Volatile private var currentMaxAmplitude = 0

    companion object {
        private const val TAG = "MediaRecorderEngine"
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BIT_RATE = 128_000
        private const val GAIN = 6.0f  // amplify quiet emulator mic
    }

    override fun start(outputFilePath: String) {
        outputPath = outputFilePath
        startTimeMs = System.currentTimeMillis()
        accumulatedMs = 0L

        val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = minBuffer * 4

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        val aacFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, SAMPLE_RATE, 1).apply {
            setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize)
        }

        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC).also {
            it.configure(aacFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            it.start()
        }

        mediaMuxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        audioRecord!!.startRecording()
        isRecording = true
        isPaused = false

        recordingThread = Thread { recordingLoop(bufferSize / 2) }.also { it.start() }
    }

    private fun recordingLoop(samplesPerRead: Int) {
        val codec = mediaCodec ?: return
        val muxer = mediaMuxer ?: return
        val ar = audioRecord ?: return

        val pcmBuffer = ShortArray(samplesPerRead)
        val codecInfo = MediaCodec.BufferInfo()
        var audioTrackIndex = -1
        var muxerStarted = false
        var presentationTimeUs = 0L

        while (isRecording) {
            if (isPaused) {
                Thread.sleep(10)
                continue
            }

            val read = ar.read(pcmBuffer, 0, samplesPerRead)
            if (read <= 0) continue

            // Apply gain + track amplitude
            var localMax = 0
            for (i in 0 until read) {
                val amplified = (pcmBuffer[i] * GAIN).roundToInt().coerceIn(-32768, 32767).toShort()
                pcmBuffer[i] = amplified
                val a = abs(amplified.toInt())
                if (a > localMax) localMax = a
            }
            currentMaxAmplitude = localMax

            // Feed PCM into codec
            val inputIndex = codec.dequeueInputBuffer(10_000)
            if (inputIndex >= 0) {
                val inputBuffer: ByteBuffer = codec.getInputBuffer(inputIndex)!!
                inputBuffer.clear()
                for (i in 0 until read) inputBuffer.putShort(pcmBuffer[i])
                codec.queueInputBuffer(inputIndex, 0, read * 2, presentationTimeUs, 0)
                presentationTimeUs += (read * 1_000_000L) / SAMPLE_RATE
            }

            // Drain encoder output
            while (true) {
                val outIndex = codec.dequeueOutputBuffer(codecInfo, 0)
                when {
                    outIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> break
                    outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        audioTrackIndex = muxer.addTrack(codec.outputFormat)
                        muxer.start()
                        muxerStarted = true
                    }
                    outIndex >= 0 -> {
                        val outBuffer = codec.getOutputBuffer(outIndex)!!
                        if (muxerStarted && codecInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                            muxer.writeSampleData(audioTrackIndex, outBuffer, codecInfo)
                        }
                        codec.releaseOutputBuffer(outIndex, false)
                    }
                }
            }
        }

        // Flush remaining frames
        val inputIndex = codec.dequeueInputBuffer(10_000)
        if (inputIndex >= 0) {
            codec.queueInputBuffer(inputIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
        }
        while (true) {
            val outIndex = codec.dequeueOutputBuffer(codecInfo, 10_000)
            if (outIndex < 0) break
            if (muxerStarted && codecInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                muxer.writeSampleData(audioTrackIndex, codec.getOutputBuffer(outIndex)!!, codecInfo)
            }
            codec.releaseOutputBuffer(outIndex, false)
            if (codecInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break
        }
    }

    override fun pause() {
        isPaused = true
        accumulatedMs += System.currentTimeMillis() - startTimeMs
    }

    override fun resume() {
        isPaused = false
        startTimeMs = System.currentTimeMillis()
    }

    override fun stop(): RecordingResult {
        val duration = accumulatedMs + (System.currentTimeMillis() - startTimeMs)
        isRecording = false
        recordingThread?.join(3_000)

        try {
            mediaCodec?.stop()
            mediaCodec?.release()
            mediaMuxer?.stop()
            mediaMuxer?.release()
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recorder", e)
        } finally {
            mediaCodec = null
            mediaMuxer = null
            audioRecord = null
            recordingThread = null
        }

        return RecordingResult(
            filePath = outputPath,
            durationMs = duration,
            sizeBytes = File(outputPath).length()
        )
    }

    override fun release() {
        isRecording = false
        recordingThread?.join(1_000)
        mediaCodec?.release()
        mediaMuxer?.release()
        audioRecord?.release()
        mediaCodec = null
        mediaMuxer = null
        audioRecord = null
        recordingThread = null
    }

    override fun getMaxAmplitude(): Int = currentMaxAmplitude.also { currentMaxAmplitude = 0 }
}
