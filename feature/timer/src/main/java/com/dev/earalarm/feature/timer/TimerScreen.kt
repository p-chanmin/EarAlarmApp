package com.dev.earalarm.feature.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.feature.timer.home.HomeScreen
import com.dev.earalarm.feature.timer.measure.MeasureScreen
import com.dev.earalarm.feature.timer.model.TimerState

@Composable
internal fun TimerScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel(),
) {
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        timerViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    when (timerState) {
        is TimerState.Home -> {
            HomeScreen(
                paddingValues = paddingValues,
                onShowErrorSnackBar = onShowErrorSnackBar,
                navigateToSetting = navigateToSetting
            )
        }

        is TimerState.Measure -> {
            MeasureScreen(
                paddingValues = paddingValues,
                onShowErrorSnackBar = onShowErrorSnackBar,
            )
        }

        is TimerState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = EarAlarmMaterialTheme.colorScheme.active
                )
            }
        }
    }
}