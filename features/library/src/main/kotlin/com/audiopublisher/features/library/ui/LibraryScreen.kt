package com.audiopublisher.features.library.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.audiopublisher.core.database.model.Recording
import com.audiopublisher.features.library.LibraryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onStartRecording: () -> Unit,
    onOpenRecording: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val recordings by viewModel.recordings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Recordings") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onStartRecording) {
                Icon(Icons.Default.Mic, contentDescription = "Record")
            }
        }
    ) { padding ->
        if (recordings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No recordings yet. Tap the mic to start.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(recordings, key = { it.id }) { recording ->
                    RecordingItem(
                        recording = recording,
                        onClick = { onOpenRecording(recording.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun RecordingItem(recording: Recording, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(recording.title) },
        supportingContent = {
            Column {
                Text(formatDuration(recording.durationMs))
                Text(formatDate(recording.createdAt))
            }
        }
    )
}

private fun formatDuration(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
