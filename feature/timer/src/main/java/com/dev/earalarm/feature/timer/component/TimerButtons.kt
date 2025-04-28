package com.dev.earalarm.feature.timer.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier,
    orientation: Int = Configuration.ORIENTATION_PORTRAIT,
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

    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_UNDEFINED -> {
            Column(
                modifier = modifier
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
                        id = R.string.feature_timer_1_hour_plus,
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
                        id = R.string.feature_timer_30_minute_plus,
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
                        id = R.string.feature_timer_10_minute_plus,
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
                        id = R.string.feature_timer_5_minute_plus,
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
                        id = R.string.feature_timer_start,
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = Paddings.small),
                        enabled = homeUiState.hour != 0 || homeUiState.minute != 0,
                        onClick = startTimerAlarm
                    )

                    PrimaryButton(
                        id = R.string.feature_timer_setting_alarm_text,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Paddings.small),
                        onClick = navigateToSetting
                    )
                    PrimaryButton(
                        id = R.string.feature_timer_reset,
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

        else -> { // Configuration.ORIENTATION_LANDSCAPE
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(vertical = Paddings.xlarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Paddings.large)
            ) {
                Row(
                    modifier = Modifier.weight(4f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = Paddings.small),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Paddings.large)
                    ) {
                        PrimaryButton(
                            id = R.string.feature_timer_1_hour_plus,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            coroutineScope.launch {
                                val index = hourState.firstVisibleItemIndex + 1
                                hourState.scrollToItem(index)
                            }
                        }
                        PrimaryButton(
                            id = R.string.feature_timer_30_minute_plus,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
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
                            id = R.string.feature_timer_10_minute_plus,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
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
                            id = R.string.feature_timer_5_minute_plus,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = Paddings.small),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Paddings.large)
                    ) {
                        PrimaryButton(
                            id = R.string.feature_timer_setting_alarm_text,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            onClick = navigateToSetting
                        )
                        PrimaryButton(
                            id = R.string.feature_timer_reset,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            coroutineScope.launch {
                                minuteState.scrollToItem(initialMinuteIndex)
                                hourState.scrollToItem(initialHourIndex)
                            }
                        }
                    }
                }
                PrimaryButton(
                    id = R.string.feature_timer_start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Paddings.small),
                    enabled = homeUiState.hour != 0 || homeUiState.minute != 0,
                    onClick = startTimerAlarm
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    locale = "ko"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    locale = "ko"
)
@Composable
private fun TimerControlButtonsPortraitPreview() {
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    heightDp = 360,
    locale = "ko"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    heightDp = 360,
    locale = "ko"
)
@Composable
private fun TimerControlButtonsLandscapePreview() {
    EarAlarmTheme {
        TimerControlButtons(
            orientation = Configuration.ORIENTATION_LANDSCAPE,
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