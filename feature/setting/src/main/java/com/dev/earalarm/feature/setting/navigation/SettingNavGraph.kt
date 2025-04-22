package com.dev.earalarm.feature.setting.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dev.earalarm.core.navigation.Route
import com.dev.earalarm.feature.setting.SettingScreen

fun NavController.navigateToSetting() {
    navigate(Route.Setting)
}

fun NavGraphBuilder.settingNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable<Route.Setting> {
        SettingScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = popBackStack,
        )
    }
}