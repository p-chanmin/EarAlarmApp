package com.dev.earalarm.feature.timer.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R
import com.dev.earalarm.feature.timer.model.HomeUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@Composable
internal fun TimerControlButtons(
    homeUiState: HomeUiState,
    hours: ImmutableList<Int>,
    minutes: ImmutableList<Int>,
    initialHourIndex: Int,
    initialMinuteIndex: Int,
    hourState: LazyListState,
    minuteState: LazyListState,
    unfocusedCount: Int,
    startTimerAlarm: () -> Unit,
    navigateToSetting: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

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
                    val index = hourState.firstVisibleItemIndex + 1
                    hourState.scrollToItem(index)
                }
            }
            PrimaryButton(
                id = R.string.timer_30_minute_plus,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Paddings.small)
            ) {
                coroutineScope.launch {
                    val min =
                        minutes[(minuteState.firstVisibleItemIndex + unfocusedCount) % minutes.size]
                    if (min >= 30) {
                        val hourIndex = hourState.firstVisibleItemIndex + 1
                        hourState.scrollToItem(hourIndex)
                    }
                    val minuteIndex = minuteState.firstVisibleItemIndex + 30
                    minuteState.scrollToItem(minuteIndex)
                }
            }
            PrimaryButton(
                id = R.string.timer_10_minute_plus,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Paddings.small)
            ) {
                coroutineScope.launch {
                    val min =
                        minutes[(minuteState.firstVisibleItemIndex + unfocusedCount) % minutes.size]
                    if (min >= 50) {
                        hourState.scrollToItem(hourState.firstVisibleItemIndex + 1)
                    }
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
                    val min =
                        minutes[(minuteState.firstVisibleItemIndex + unfocusedCount) % minutes.size]
                    if (min >= 55) {
                        hourState.scrollToItem(hourState.firstVisibleItemIndex + 1)
                    }
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
                enabled = homeUiState.hour != 0 || homeUiState.minute != 0,
                onClick = startTimerAlarm
            )

            PrimaryButton(
                id = R.string.setting_alarm_text,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Paddings.small),
                onClick = navigateToSetting
            )
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Composable
private fun TimerControlButtonsPreview() {
    EarAlarmTheme {
        TimerControlButtons(
            homeUiState = HomeUiState(),
            hours = (0..99).toPersistentList(),
            minutes = (0..59).toPersistentList(),
            initialHourIndex = 123,
            initialMinuteIndex = 123,
            hourState = LazyListState(),
            minuteState = LazyListState(),
            unfocusedCount = 1,
            startTimerAlarm = {},
            navigateToSetting = {},
        )
    }
}