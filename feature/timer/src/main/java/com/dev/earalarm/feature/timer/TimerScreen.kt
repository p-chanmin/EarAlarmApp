package com.dev.earalarm.feature.timer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.feature.timer.home.HomeScreen
import com.dev.earalarm.feature.timer.measure.MeasureScreen

@Composable
internal fun TimerScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel(),
) {

    val hasTimer by timerViewModel.hasTimer.collectAsStateWithLifecycle()

    if (!hasTimer) {
        HomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToSetting = navigateToSetting
        )
    } else {
        MeasureScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
        )
    }
}