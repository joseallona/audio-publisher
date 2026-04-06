package com.audiopublisher.core.media

interface RecorderEngine {
    fun start(outputFilePath: String)
    fun pause()
    fun resume()
    fun stop(): RecordingResult
    fun release()
}

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val sizeBytes: Long
)
