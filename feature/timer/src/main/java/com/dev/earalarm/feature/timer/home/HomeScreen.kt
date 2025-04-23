package com.dev.earalarm.feature.timer.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R
import com.dev.earalarm.feature.timer.component.PermissionDialog
import com.dev.earalarm.feature.timer.component.PrimaryButton
import com.dev.earalarm.feature.timer.component.WheelPicker
import com.dev.earalarm.feature.timer.model.HomeUiState
import com.dev.earalarm.feature.timer.model.PermissionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Composable
internal fun HomeScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    HomeContent(
        homeUiState = homeUiState,
        paddingValues = paddingValues,
        onShowErrorSnackBar = onShowErrorSnackBar,
        navigateToSetting = navigateToSetting,
        confirmExactAlarmPermission = {},
        updateNotificationPermissionState = homeViewModel::updateNotificationPermissionState,
        audioFilePickerLauncher = {},
        dismissDialog = homeViewModel::dismissDialog,
        startTimerAlarm = homeViewModel::startTimerAlarm,
        updateTimerAlarm = homeViewModel::updateTimerAlarm,
    )
}

@Composable
private fun HomeContent(
    homeUiState: HomeUiState,
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
    confirmExactAlarmPermission: () -> Unit,
    updateNotificationPermissionState: (PermissionState, Boolean) -> Unit,
    audioFilePickerLauncher: () -> Unit,
    dismissDialog: () -> Unit,
    startTimerAlarm: () -> Unit,
    updateTimerAlarm: (Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isPreview = LocalInspectionMode.current

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            updateNotificationPermissionState(PermissionState.GRANTED, false)
        } else {
            val shouldShowRationale = permissions.keys.any {
                shouldShowRequestPermissionRationale(context as Activity, it)
            }
            if (shouldShowRationale) {
                updateNotificationPermissionState(PermissionState.DENIED, true)
            } else {
                updateNotificationPermissionState(PermissionState.DENIED, false)
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPreview) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        val requiredPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                        permissionsLauncher.launch(requiredPermissions)
                    }

                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val hours = (0..99).toList()
        val minutes = (0..59).toList()
        val initialHourIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % hours.size) - 1
        val initialMinuteIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % minutes.size) - 1
        val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHourIndex)
        val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)
        val unfocusedCount = 1
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(hourState, minuteState) {
            val hourFlow = snapshotFlow { hourState.firstVisibleItemIndex }
            val minuteFlow = snapshotFlow { minuteState.firstVisibleItemIndex }

            combine(hourFlow, minuteFlow) { hourIndex, minuteIndex ->
                Pair(
                    hours[(hourIndex + unfocusedCount) % hours.size],
                    minutes[(minuteIndex + unfocusedCount) % minutes.size]
                )
            }.collectLatest { (hour, minute) ->
                updateTimerAlarm(hour, minute)
            }
        }

        if (homeUiState.deniedExactAlarmDialog) {
            PermissionDialog(
                titleId = R.string.permission_exact_alarm_title_request,
                contentTextId = R.string.permission_exact_alarm_message_request,
                dismissButtonTextId = R.string.permission_negative,
                confirmButtonTextId = R.string.permission_positive,
                onDismiss = {
                    dismissDialog()
                },
                onConfirm = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:${context.packageName}"),
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                    dismissDialog()
                }
            )
        }

        if (homeUiState.deniedNotificationDialog) {
            PermissionDialog(
                titleId = R.string.permission_notification_title_request,
                contentTextId = R.string.permission_notification_message_request,
                dismissButtonTextId = R.string.permission_negative,
                confirmButtonTextId = R.string.permission_positive,
                onDismiss = {
                    onShowErrorSnackBar(
                        Throwable(message = context.getString(R.string.permission_notification_denied))
                    )
                    dismissDialog()
                },
                onConfirm = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val requiredPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                        permissionsLauncher.launch(requiredPermissions)
                    }
                    dismissDialog()
                }
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
                        homeUiState.hour * 60 + homeUiState.minute
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )

                Text(
                    text = stringResource(
                        id = R.string.timer_info_end_time,
                        homeUiState.estimatedEndTime
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = Paddings.xlarge)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelPicker(
                        modifier = Modifier,
                        list = hours,
                        state = hourState,
                        itemWidth = 80.dp,
                        itemHeight = 50.dp,
                        unfocusedCount = unfocusedCount,
                    ) { i ->
                        Text(
                            text = hours[i].toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = ":"
                    )
                    WheelPicker(
                        modifier = Modifier,
                        list = minutes,
                        state = minuteState,
                        itemWidth = 80.dp,
                        itemHeight = 50.dp,
                        unfocusedCount = unfocusedCount,
                    ) { i ->
                        Text(
                            text = hours[i].toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = Paddings.large, vertical = Paddings.medium)
            ) {
                if (homeUiState.notificationPermissionState == PermissionState.DENIED) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .clickable {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                        text = stringResource(
                            id = R.string.permission_notification_denied_text
                        ),
                        style = MaterialTheme.typography.labelSmall
                            .copy(color = MaterialTheme.colorScheme.onSecondary),
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clickable { navigateToSetting() },
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.setting_alarm_volume,
                            homeUiState.volume
                        ),
                        style = MaterialTheme.typography.labelSmall
                            .copy(color = MaterialTheme.colorScheme.onSecondary),
                    )
                    Text(
                        text = stringResource(
                            id = R.string.setting_alarm_media,
                            if (homeUiState.alarmMedia != null) {
                                homeUiState.alarmMedia.name
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
                        val min =
                            minutes[(minuteState.firstVisibleItemIndex + unfocusedCount) % minutes.size]
                        if (min >= 30) {
                            hourState.scrollToItem(hourState.firstVisibleItemIndex + 1)
                        }
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
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Composable
private fun HomeContentPreview() {
    EarAlarmTheme {
        HomeContent(
            homeUiState = HomeUiState(
                notificationPermissionState = PermissionState.DENIED
            ),
            paddingValues = PaddingValues(),
            onShowErrorSnackBar = {},
            navigateToSetting = {},
            confirmExactAlarmPermission = {},
            updateNotificationPermissionState = { _, _ -> },
            audioFilePickerLauncher = {},
            dismissDialog = {},
            startTimerAlarm = {},
            updateTimerAlarm = { _, _ -> }
        )
    }
}