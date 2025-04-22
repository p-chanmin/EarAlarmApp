package com.dev.earalarm.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dev.earalarm.core.navigation.Route
import com.dev.earalarm.feature.timer.navigation.navigateToTimer
import com.dev.earalarm.feature.setting.navigation.navigateToSetting

internal class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination = Route.Timer

    fun navigateToTimer() {
        navController.navigateToTimer()
    }

    fun navigateToSetting() {
        navController.navigateToSetting()
    }

    private fun popBackStack() {
        navController.popBackStack()
    }

    fun popBackStackIfNotStartDestination() {
        if (!isSameCurrentDestination<Route.Timer>()) {
            popBackStack()
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean {
        val currentRoute = navController.currentDestination?.route
        return currentRoute == T::class.simpleName
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
