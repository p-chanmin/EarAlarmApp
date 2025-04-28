package com.dev.earalarm.feature.timer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.dev.earalarm.core.navigation.DEEP_LINK_BASE_PATH
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
    composable<Route.Timer>(
        deepLinks = listOf(navDeepLink<Route.Timer>(basePath = DEEP_LINK_BASE_PATH + "timer"))
    ) {
        TimerScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToSetting = navigateToSetting,
        )
    }
}