package com.audiopublisher.features.recording.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startRecording() else permissionDenied = true
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) viewModel.startRecording()
        else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(state.status) {
        if (state.status == RecordingStatus.STOPPED) onNavigateBack()
    }

    if (permissionDenied) {
        AlertDialog(
            onDismissRequest = onNavigateBack,
            title = { Text("Microphone permission required") },
            text = { Text("Grant microphone access in Settings to record audio.") },
            confirmButton = { TextButton(onClick = onNavigateBack) { Text("OK") } }
        )
        return
    }

    Scaffold { _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                if (state.status == RecordingStatus.RECORDING) {
                    Text(
                        text = "● REC",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Text(
                        text = "⏸ PAUSED",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Text(
                    text = formatElapsed(state.elapsedSeconds),
                    style = MaterialTheme.typography.displayMedium
                )

                WaveformVisualizer(
                    amplitudeLevels = state.amplitudeLevels,
                    isRecording = state.status == RecordingStatus.RECORDING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
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

@Composable
private fun WaveformVisualizer(
    amplitudeLevels: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isRecording) Color.Red else Color.Gray

    Canvas(modifier = modifier) {
        val levels = if (amplitudeLevels.isEmpty()) List(40) { 0.05f } else amplitudeLevels
        val barCount = levels.size
        val totalWidth = size.width
        val barWidth = (totalWidth / barCount) * 0.6f
        val gap = (totalWidth / barCount) * 0.4f
        val centerY = size.height / 2f
        val maxBarHalfHeight = size.height / 2f

        levels.forEachIndexed { index, level ->
            val x = index * (barWidth + gap) + barWidth / 2f
            val halfHeight = (level * maxBarHalfHeight).coerceAtLeast(4f)
            drawLine(
                color = color,
                start = Offset(x, centerY - halfHeight),
                end = Offset(x, centerY + halfHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun formatElapsed(seconds: Long): String {
    val h = TimeUnit.SECONDS.toHours(seconds)
    val m = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
