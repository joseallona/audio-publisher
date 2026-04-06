package com.audiopublisher.features.recording.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.audiopublisher.features.recording.ui.RecordingScreen

fun NavGraphBuilder.recordingGraph(
    onNavigateBack: () -> Unit
) {
    composable("recording") {
        RecordingScreen(onNavigateBack = onNavigateBack)
    }
}
