package kr.ac.tukorea.android.earalarm.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.presentation.main.input.IMainViewModelInputs
import kr.ac.tukorea.android.earalarm.presentation.main.output.AlarmUiState
import kr.ac.tukorea.android.earalarm.ui.components.buttons.PrimaryButton
import kr.ac.tukorea.android.earalarm.ui.components.dialogs.PermissionDialog
import kr.ac.tukorea.android.earalarm.ui.components.dialogs.SettingDialog
import kr.ac.tukorea.android.earalarm.ui.components.picker.TimePicker
import kr.ac.tukorea.android.earalarm.ui.theme.Paddings

@Composable
fun AlarmScreen(
    alarmUiState: State<AlarmUiState>,
    input: IMainViewModelInputs,
    confirmExactAlarmPermission: () -> Unit,
    confirmNotificationPermission: () -> Unit,
    audioFilePickerLauncher: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val hours = (0..99).toList()
        val minutes = (0..59).toList()
        val initialHourIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % hours.size) - 1
        val initialMinuteIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % minutes.size) - 1
        val hourState =
            rememberLazyListState(initialFirstVisibleItemIndex = initialHourIndex)
        val minuteState =
            rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)
        val coroutineScope = rememberCoroutineScope()

        if (alarmUiState.value.deniedExactAlarmDialog) {
            PermissionDialog(
                titleId = R.string.permission_exact_alarm_title_request,
                contentTextId = R.string.permission_exact_alarm_message_request,
                dismissButtonTextId = R.string.permission_negative,
                confirmButtonTextId = R.string.permission_positive,
                onDismiss = { input.dismissDialog() },
                onConfirm = {
                    confirmExactAlarmPermission()
                    input.dismissDialog()
                }
            )
        }

        if (alarmUiState.value.deniedNotificationDialog) {
            PermissionDialog(
                titleId = R.string.permission_notification_title_request,
                contentTextId = R.string.permission_notification_message_request,
                dismissButtonTextId = R.string.permission_negative,
                confirmButtonTextId = R.string.permission_positive,
                onDismiss = { input.dismissDialog() },
                onConfirm = {
                    confirmNotificationPermission()
                    input.dismissDialog()
                }
            )
        }

        if (alarmUiState.value.alarmSettingDialog) {
            SettingDialog(
                alarmUiState = alarmUiState,
                onAlarmSoundSetting = { audioFilePickerLauncher() },
                onChangeAlarmVolume = { volume -> input.setVolume(volume) },
                onConfirm = { input.dismissDialog() }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        id = R.string.timer_info_minute,
                        alarmUiState.value.hour * 60 + alarmUiState.value.minute
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )

                Text(
                    text = stringResource(
                        id = R.string.timer_info_end_time,
                        alarmUiState.value.estimatedEndTime
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = Paddings.xlarge)
                )

                TimePicker(
                    hours,
                    minutes,
                    hourState,
                    minuteState,
                ) { hour, minute ->
                    input.updateTimePicker(hour, minute)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = Paddings.large, vertical = Paddings.medium),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(
                        id = R.string.setting_alarm_volume,
                        alarmUiState.value.volume
                    ),
                    style = MaterialTheme.typography.labelSmall
                        .copy(color = MaterialTheme.colorScheme.onSecondary),
                )
                Text(
                    text = stringResource(
                        id = R.string.setting_alarm_media,
                        if (alarmUiState.value.alarmMedia != null) {
                            alarmUiState.value.alarmMedia!!.name
                        } else {
                            stringResource(id = R.string.setting_default_sound)
                        }
                    ),
                    style = MaterialTheme.typography.labelSmall
                        .copy(color = MaterialTheme.colorScheme.onSecondary),
                    modifier = Modifier
                        .padding(top = Paddings.small)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Paddings.xlarge)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrimaryButton(
                    id = R.string.timer_1_hour_plus,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    coroutineScope.launch {
                        hourState.scrollToItem(hourState.firstVisibleItemIndex + 1)
                    }
                }
                PrimaryButton(
                    id = R.string.timer_30_minute_plus,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    coroutineScope.launch {
                        minuteState.scrollToItem(minuteState.firstVisibleItemIndex + 30)
                    }
                }
                PrimaryButton(
                    id = R.string.timer_10_minute_plus,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    coroutineScope.launch {
                        minuteState.scrollToItem(minuteState.firstVisibleItemIndex + 10)
                    }
                }
                PrimaryButton(
                    id = R.string.timer_5_minute_plus,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    coroutineScope.launch {
                        minuteState.scrollToItem(minuteState.firstVisibleItemIndex + 5)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.small)
                    .padding(top = Paddings.medium),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryButton(
                    id = R.string.timer_start,
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = Paddings.small),
                    enabled = alarmUiState.value.hour != 0 || alarmUiState.value.minute != 0
                ) {
                    input.startTimerAlarm()
                }
                PrimaryButton(
                    id = R.string.setting_alarm_text,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    input.showSettingDialog()
                }
                PrimaryButton(
                    id = R.string.timer_reset,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Paddings.small)
                ) {
                    coroutineScope.launch {
                        minuteState.scrollToItem(initialMinuteIndex)
                        hourState.scrollToItem(initialHourIndex)
                    }
                }
            }
        }
    }
}

//@Preview(locale = "ko", showBackground = true, name = "Portrait")
//@Composable
//fun AlarmScreenPortraitPreview() {
//    EarAlarmTheme {
//        AlarmScreen()
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
//fun AlarmScreenLandscapePreview() {
//    EarAlarmTheme {
//        AlarmScreen()
//    }
//}