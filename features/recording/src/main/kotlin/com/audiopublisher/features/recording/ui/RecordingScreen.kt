package com.audiopublisher.features.recording.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.drawscope.Stroke
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

    val isRecording = state.status == RecordingStatus.RECORDING

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

                // Pulsing mic indicator
                PulsingRecordIndicator(isRecording = isRecording)

                Text(
                    text = formatElapsed(state.elapsedSeconds),
                    style = MaterialTheme.typography.displayMedium
                )

                // Live waveform
                WaveformVisualizer(
                    amplitudeLevels = state.amplitudeLevels,
                    isRecording = isRecording,
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
private fun PulsingRecordIndicator(isRecording: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isRecording) 0f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            if (isRecording) {
                drawCircle(
                    color = Color.Red.copy(alpha = pulseAlpha),
                    radius = radius * pulseScale,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
            drawCircle(
                color = if (isRecording) Color.Red else Color.Gray,
                radius = radius * 0.5f
            )
        }
    }
}

@Composable
private fun WaveformVisualizer(
    amplitudeLevels: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isRecording) Color.Red else Color.Gray.copy(alpha = 0.4f)
    val levels = if (amplitudeLevels.isEmpty()) List(40) { 0f } else amplitudeLevels

    Canvas(modifier = modifier) {
        val barCount = levels.size
        val barWidth = (size.width / barCount) * 0.6f
        val gap = (size.width / barCount) * 0.4f
        val centerY = size.height / 2f
        val maxHalf = size.height / 2f

        levels.forEachIndexed { index, level ->
            val x = index * (barWidth + gap) + barWidth / 2f
            val halfHeight = (level * maxHalf).coerceAtLeast(if (isRecording) 4f else 2f)
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
