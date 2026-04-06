package com.audiopublisher.features.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.core.database.repository.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: RecordingRepository
) : ViewModel() {

    val recordings: StateFlow<List<Recording>> = repository
        .getAllActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
