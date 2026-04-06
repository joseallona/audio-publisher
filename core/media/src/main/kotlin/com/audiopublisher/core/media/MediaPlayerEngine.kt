package com.audiopublisher.core.media

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaPlayerEngine @Inject constructor() : PlayerEngine {

    private var player: MediaPlayer? = null
    private val _state = MutableStateFlow(PlayerState())
    override val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var positionJob: Job? = null

    override fun play(filePath: String) {
        release()
        val mp = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            setOnCompletionListener {
                positionJob?.cancel()
                _state.value = _state.value.copy(
                    status = PlayerStatus.COMPLETED,
                    currentPositionMs = 0L
                )
            }
        }
        player = mp
        _state.value = PlayerState(
            status = PlayerStatus.PLAYING,
            currentPositionMs = 0L,
            durationMs = mp.duration.toLong()
        )
        startPositionPolling()
    }

    override fun pause() {
        positionJob?.cancel()
        player?.pause()
        _state.value = _state.value.copy(
            status = PlayerStatus.PAUSED,
            currentPositionMs = player?.currentPosition?.toLong() ?: 0L
        )
    }

    override fun resume() {
        player?.start()
        _state.value = _state.value.copy(status = PlayerStatus.PLAYING)
        startPositionPolling()
    }

    override fun seekTo(ms: Long) {
        player?.seekTo(ms.toInt())
        _state.value = _state.value.copy(currentPositionMs = ms)
    }

    override fun release() {
        positionJob?.cancel()
        player?.release()
        player = null
        _state.value = PlayerState()
    }

    private fun startPositionPolling() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (true) {
                delay(200)
                val p = player ?: break
                if (_state.value.status != PlayerStatus.PLAYING) break
                _state.value = _state.value.copy(
                    currentPositionMs = p.currentPosition.toLong()
                )
            }
        }
    }
}
