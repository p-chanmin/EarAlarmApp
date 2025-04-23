package com.dev.earalarm.feature.timer.measure

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R
import com.dev.earalarm.feature.timer.component.PrimaryButton
import com.dev.earalarm.feature.timer.model.MeasureUiState

@Composable
internal fun MeasureScreen(
    measureViewModel: MeasureViewModel = hiltViewModel()
) {
    val measureUiState by measureViewModel.measureUiState.collectAsStateWithLifecycle()

    MeasureContent(
        measureUiState = measureUiState,
        dismissTimerAlarm = measureViewModel::dismissTimerAlarm,
    )
}

@Composable
private fun MeasureContent(
    measureUiState: MeasureUiState,
    dismissTimerAlarm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = measureUiState.leftTime,
            style = MaterialTheme.typography.headlineLarge
        )

        LinearProgressIndicator(
            modifier = Modifier.padding(vertical = Paddings.large),
            progress = measureUiState.progress,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.medium),
            text = stringResource(id = R.string.alarm_info_minute, measureUiState.minute),
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.medium),
            text = stringResource(
                id = R.string.timer_info_end_time,
                measureUiState.endTimeString
            ),
            style = MaterialTheme.typography.headlineSmall
        )
        PrimaryButton(
            modifier = Modifier.padding(top = Paddings.large),
            id = R.string.dismiss_alarm,
            onClick = dismissTimerAlarm
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Composable
private fun MeasureContentPreview() {
    EarAlarmTheme {
        MeasureContent(
            measureUiState = MeasureUiState(),
            dismissTimerAlarm = {},
        )
    }
}