package com.audiopublisher.core.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class MediaRecorderEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : RecorderEngine {

    private var recorder: MediaRecorder? = null
    private var startTimeMs: Long = 0L
    private var accumulatedMs: Long = 0L
    private var outputPath: String = ""

    override fun start(outputFilePath: String) {
        outputPath = outputFilePath
        recorder = createRecorder().apply {
            setOutputFile(outputFilePath)
            prepare()
            start()
        }
        startTimeMs = System.currentTimeMillis()
        accumulatedMs = 0L
    }

    override fun pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.pause()
            accumulatedMs += System.currentTimeMillis() - startTimeMs
        }
    }

    override fun resume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder?.resume()
            startTimeMs = System.currentTimeMillis()
        }
    }

    override fun stop(): RecordingResult {
        val duration = accumulatedMs + (System.currentTimeMillis() - startTimeMs)
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        val size = File(outputPath).length()
        return RecordingResult(
            filePath = outputPath,
            durationMs = duration,
            sizeBytes = size
        )
    }

    override fun release() {
        recorder?.release()
        recorder = null
    }

    override fun getMaxAmplitude(): Int = recorder?.maxAmplitude ?: 0

    private fun createRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128_000)
        }
}
