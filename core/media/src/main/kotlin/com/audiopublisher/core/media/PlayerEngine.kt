package com.audiopublisher.core.media

import kotlinx.coroutines.flow.StateFlow

interface PlayerEngine {
    val state: StateFlow<PlayerState>
    fun play(filePath: String)
    fun pause()
    fun resume()
    fun seekTo(ms: Long)
    fun release()
}

data class PlayerState(
    val status: PlayerStatus = PlayerStatus.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L
)

enum class PlayerStatus {
    IDLE, PLAYING, PAUSED, COMPLETED
}
