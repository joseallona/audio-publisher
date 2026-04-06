package com.audiopublisher.core.media

interface RecorderEngine {
    fun start(outputFilePath: String)
    fun pause()
    fun resume()
    fun stop(): RecordingResult
    fun release()
    fun getMaxAmplitude(): Int
}

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val sizeBytes: Long
)
