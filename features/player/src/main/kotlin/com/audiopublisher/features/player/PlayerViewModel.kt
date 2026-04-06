package com.audiopublisher.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.repository.RecordingRepository
import com.audiopublisher.core.media.PlayerEngine
import com.audiopublisher.core.media.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: RecordingRepository,
    private val playerEngine: PlayerEngine
) : ViewModel() {

    private val _recording = MutableStateFlow<Recording?>(null)
    val recording: StateFlow<Recording?> = _recording.asStateFlow()

    val playerState: StateFlow<PlayerState> = playerEngine.state

    fun load(recordingId: String) {
        viewModelScope.launch {
            val r = repository.getById(recordingId) ?: return@launch
            _recording.value = r
            playerEngine.play(r.filePath)
        }
    }

    fun togglePlayPause() {
        val status = playerState.value.status
        when (status) {
            com.audiopublisher.core.media.PlayerStatus.PLAYING -> playerEngine.pause()
            com.audiopublisher.core.media.PlayerStatus.PAUSED,
            com.audiopublisher.core.media.PlayerStatus.COMPLETED -> playerEngine.resume()
            else -> Unit
        }
    }

    fun seekTo(ms: Long) = playerEngine.seekTo(ms)

    fun delete(onDeleted: () -> Unit) {
        val id = _recording.value?.id ?: return
        viewModelScope.launch {
            repository.delete(id)
            playerEngine.release()
            onDeleted()
        }
    }

    fun rename(newTitle: String) {
        val id = _recording.value?.id ?: return
        viewModelScope.launch {
            repository.updateTitle(id, newTitle)
            _recording.value = _recording.value?.copy(title = newTitle)
        }
    }

    fun getFilePath(): String? = _recording.value?.filePath
    fun getTitle(): String? = _recording.value?.title

    override fun onCleared() {
        playerEngine.release()
    }
}
