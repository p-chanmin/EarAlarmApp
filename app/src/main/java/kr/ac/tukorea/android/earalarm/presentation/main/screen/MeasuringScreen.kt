package kr.ac.tukorea.android.earalarm.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.presentation.main.input.IMainViewModelInputs
import kr.ac.tukorea.android.earalarm.presentation.main.output.MeasuringUiState
import kr.ac.tukorea.android.earalarm.ui.components.buttons.PrimaryButton
import kr.ac.tukorea.android.earalarm.ui.theme.Paddings

@Composable
fun MeasuringScreen(
    measuringUiState: State<MeasuringUiState>,
    input: IMainViewModelInputs
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = measuringUiState.value.leftTime,
            style = MaterialTheme.typography.headlineLarge
        )

        LinearProgressIndicator(
            modifier = Modifier.padding(vertical = Paddings.large),
            progress = measuringUiState.value.progress,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.medium),
            text = stringResource(id = R.string.alarm_info_minute, measuringUiState.value.minute),
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.medium),
            text = stringResource(
                id = R.string.timer_info_end_time,
                measuringUiState.value.endTimeString
            ),
            style = MaterialTheme.typography.headlineSmall
        )

        PrimaryButton(
            modifier = Modifier.padding(top = Paddings.large),
            id = R.string.dismiss_alarm
        ) {
            input.dismissAlarm()
        }
    }
}

//@Preview(locale = "ko", showBackground = true, name = "Portrait")
//@Composable
//fun MeasuringScreenScreenPortraitPreview() {
//    EarAlarmTheme {
//        MeasuringScreen()
//    }
//}
//
//@Preview(
//    locale = "ko",
//    showBackground = true,
//    name = "Landscape",
//    widthDp = 800,
//    heightDp = 340
//)
//@Composable
//fun MeasuringScreenScreenLandscapePreview() {
//    EarAlarmTheme {
//        MeasuringScreen()
//    }
//}