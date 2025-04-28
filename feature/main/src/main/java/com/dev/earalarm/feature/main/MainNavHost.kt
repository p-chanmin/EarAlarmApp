package com.dev.earalarm.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.dev.earalarm.feature.setting.navigation.settingNavGraph
import com.dev.earalarm.feature.timer.navigation.timerNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        timerNavGraph(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToSetting = navigator::navigateToSetting
        )

        settingNavGraph(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = navigator::popBackStackIfNotStartDestination
        )
    }
}