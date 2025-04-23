package com.dev.earalarm.feature.timer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dev.earalarm.core.navigation.Route
import com.dev.earalarm.feature.timer.TimerScreen

fun NavController.navigateToTimer() {
    navigate(Route.Timer)
}

fun NavGraphBuilder.timerNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
) {
    composable<Route.Timer> {
        TimerScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToSetting = navigateToSetting,
        )
    }
}