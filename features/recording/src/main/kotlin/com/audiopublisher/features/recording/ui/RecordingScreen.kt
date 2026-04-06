package com.audiopublisher.features.recording.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.audiopublisher.features.recording.RecordingStatus
import com.audiopublisher.features.recording.RecordingViewModel
import java.util.concurrent.TimeUnit

@Composable
fun RecordingScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecordingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startRecording()
    }

    LaunchedEffect(state.status) {
        if (state.status == RecordingStatus.STOPPED) {
            onNavigateBack()
        }
    }

    Scaffold { _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                if (state.status == RecordingStatus.RECORDING) {
                    Text(
                        text = "● REC",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Text(
                    text = formatElapsed(state.elapsedSeconds),
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    when (state.status) {
                        RecordingStatus.RECORDING -> {
                            FilledIconButton(
                                onClick = { viewModel.pauseRecording() },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = "Pause")
                            }
                        }
                        RecordingStatus.PAUSED -> {
                            FilledIconButton(
                                onClick = { viewModel.resumeRecording() },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                            }
                        }
                        else -> Unit
                    }

                    FilledIconButton(
                        onClick = { viewModel.stopRecording() },
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    }
                }
            }
        }
    }
}

private fun formatElapsed(seconds: Long): String {
    val h = TimeUnit.SECONDS.toHours(seconds)
    val m = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
