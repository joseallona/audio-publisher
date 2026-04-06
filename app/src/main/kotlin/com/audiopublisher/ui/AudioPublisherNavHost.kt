package com.audiopublisher.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.audiopublisher.features.library.navigation.libraryGraph
import com.audiopublisher.features.library.navigation.LibraryRoute
import com.audiopublisher.features.recording.navigation.recordingGraph
import com.audiopublisher.features.player.navigation.playerGraph

@Composable
fun AudioPublisherNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LibraryRoute
    ) {
        libraryGraph(
            onStartRecording = { navController.navigate("recording") },
            onOpenRecording = { id -> navController.navigate("player/$id") }
        )
        recordingGraph(
            onNavigateBack = { navController.popBackStack() }
        )
        playerGraph(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
