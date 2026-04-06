package com.audiopublisher.features.library.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.audiopublisher.features.library.ui.LibraryScreen

const val LibraryRoute = "library"

fun NavGraphBuilder.libraryGraph(
    onStartRecording: () -> Unit,
    onOpenRecording: (String) -> Unit
) {
    composable(LibraryRoute) {
        LibraryScreen(
            onStartRecording = onStartRecording,
            onOpenRecording = onOpenRecording
        )
    }
}
