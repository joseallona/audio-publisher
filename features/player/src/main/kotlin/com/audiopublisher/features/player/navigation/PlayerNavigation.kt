package com.audiopublisher.features.player.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.audiopublisher.features.player.ui.PlayerScreen

fun NavGraphBuilder.playerGraph(
    onNavigateBack: () -> Unit
) {
    composable(
        route = "player/{recordingId}",
        arguments = listOf(navArgument("recordingId") { type = NavType.StringType })
    ) { backStackEntry ->
        val recordingId = backStackEntry.arguments?.getString("recordingId") ?: return@composable
        PlayerScreen(
            recordingId = recordingId,
            onNavigateBack = onNavigateBack
        )
    }
}
