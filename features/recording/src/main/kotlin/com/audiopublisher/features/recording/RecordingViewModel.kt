package com.audiopublisher.features.recording

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audiopublisher.features.recording.service.RecordingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(RecordingUiState())
    val state: StateFlow<RecordingUiState> = _state.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    fun startRecording() {
        val intent = Intent(context, RecordingService::class.java)
            .setAction(RecordingService.ACTION_START)
        context.startForegroundService(intent)
        _state.value = _state.value.copy(status = RecordingStatus.RECORDING)
        startTimer()
    }

    fun pauseRecording() {
        val intent = Intent(context, RecordingService::class.java)
            .setAction(RecordingService.ACTION_PAUSE)
        context.startService(intent)
        _state.value = _state.value.copy(status = RecordingStatus.PAUSED)
        timerJob?.cancel()
    }

    fun resumeRecording() {
        val intent = Intent(context, RecordingService::class.java)
            .setAction(RecordingService.ACTION_RESUME)
        context.startService(intent)
        _state.value = _state.value.copy(status = RecordingStatus.RECORDING)
        startTimer()
    }

    fun stopRecording() {
        val intent = Intent(context, RecordingService::class.java)
            .setAction(RecordingService.ACTION_STOP)
        context.startService(intent)
        timerJob?.cancel()
        _state.value = RecordingUiState(status = RecordingStatus.STOPPED)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _state.value = _state.value.copy(
                    elapsedSeconds = _state.value.elapsedSeconds + 1
                )
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}

data class RecordingUiState(
    val status: RecordingStatus = RecordingStatus.IDLE,
    val elapsedSeconds: Long = 0L
)

enum class RecordingStatus {
    IDLE, RECORDING, PAUSED, STOPPED
}
