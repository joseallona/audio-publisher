package com.audiopublisher.core.media

import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MediaPlayerEngine @Inject constructor() : PlayerEngine {

    private var player: MediaPlayer? = null
    private val _state = MutableStateFlow(PlayerState())
    override val state: StateFlow<PlayerState> = _state.asStateFlow()

    override fun play(filePath: String) {
        release()
        player = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            setOnCompletionListener {
                _state.value = _state.value.copy(
                    status = PlayerStatus.COMPLETED,
                    currentPositionMs = 0L
                )
            }
        }
        _state.value = PlayerState(
            status = PlayerStatus.PLAYING,
            currentPositionMs = 0L,
            durationMs = player?.duration?.toLong() ?: 0L
        )
    }

    override fun pause() {
        player?.pause()
        _state.value = _state.value.copy(
            status = PlayerStatus.PAUSED,
            currentPositionMs = player?.currentPosition?.toLong() ?: 0L
        )
    }

    override fun resume() {
        player?.start()
        _state.value = _state.value.copy(status = PlayerStatus.PLAYING)
    }

    override fun seekTo(ms: Long) {
        player?.seekTo(ms.toInt())
        _state.value = _state.value.copy(currentPositionMs = ms)
    }

    override fun release() {
        player?.release()
        player = null
        _state.value = PlayerState()
    }
}
