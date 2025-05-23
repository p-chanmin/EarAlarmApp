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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R
import com.dev.earalarm.feature.timer.component.PermissionDialog
import com.dev.earalarm.feature.timer.component.TimerControlButtons
import com.dev.earalarm.feature.timer.component.WheelPicker
import com.dev.earalarm.feature.timer.model.HomeUiState
import com.dev.earalarm.feature.timer.model.PermissionState
import com.dev.firebase.LocalFirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

@Composable
internal fun HomeScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToSetting: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("HomeScreen", configuration.orientation)
        homeViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    HomeContent(
        homeUiState = homeUiState,
        paddingValues = paddingValues,
        onShowErrorSnackBar = onShowErrorSnackBar,
        navigateToSetting = navigateToSetting,
        updateNotificationPermissionState = homeViewModel::updateNotificationPermissionState,
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
    updateNotificationPermissionState: (PermissionState, Boolean) -> Unit,
    dismissDialog: () -> Unit,
    startTimerAlarm: () -> Unit,
    updateTimerAlarm: (Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val firebaseManager = LocalFirebaseManager.current
    val isPreview = LocalInspectionMode.current

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            updateNotificationPermissionState(PermissionState.GRANTED, false)
            firebaseManager.firebaseAnalytics.logEvent(FA.Event.PERMISSION_RESULT) {
                param(FA.Param.Key.PERMISSION_TYPE, FA.Param.Value.NOTIFICATION)
                param(FA.Param.Key.STATUS, FA.Param.Value.GRANTED)
            }
        } else {
            val shouldShowRationale = permissions.keys.any {
                shouldShowRequestPermissionRationale(context as Activity, it)
            }
            if (shouldShowRationale) {
                updateNotificationPermissionState(PermissionState.DENIED, true)
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.PERMISSION_RESULT) {
                    param(FA.Param.Key.PERMISSION_TYPE, FA.Param.Value.NOTIFICATION)
                    param(FA.Param.Key.STATUS, FA.Param.Value.DENIED)
                    param(FA.Param.Key.SHOW_RATIONALE, "true")
                }
            } else {
                updateNotificationPermissionState(PermissionState.DENIED, false)
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.PERMISSION_RESULT) {
                    param(FA.Param.Key.PERMISSION_TYPE, FA.Param.Value.NOTIFICATION)
                    param(FA.Param.Key.STATUS, FA.Param.Value.DENIED)
                    param(FA.Param.Key.SHOW_RATIONALE, "false")
                }
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

    val navigateToNotificationPermissionSettings = {
        firebaseManager.firebaseAnalytics.logEvent(FA.Event.NAVIGATION) {
            param(FA.Param.Key.DESTINATION, FA.Param.Value.NOTIFICATION)
        }
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        context.startActivity(intent)
    }

    val navigateToExactAlarmRequest = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            firebaseManager.firebaseAnalytics.logEvent(FA.Event.NAVIGATION) {
                param(FA.Param.Key.DESTINATION, FA.Param.Value.EXACT_ALARM)
            }
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}"),
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    val hours by remember { mutableStateOf((0..99).toPersistentList()) }
    val minutes by remember { mutableStateOf((0..59).toPersistentList()) }
    val initialHourIndex by remember {
        mutableIntStateOf((Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % hours.size) - 1)
    }
    val initialMinuteIndex by remember {
        mutableIntStateOf((Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % minutes.size) - 1)
    }
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHourIndex)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)
    val unfocusedCount by remember { mutableIntStateOf(1) }

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
            titleId = R.string.feature_timer_permission_exact_alarm_title_request,
            contentTextId = R.string.feature_timer_permission_exact_alarm_message_request,
            dismissButtonTextId = R.string.feature_timer_permission_negative,
            confirmButtonTextId = R.string.feature_timer_permission_positive,
            onDismiss = {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.PERMISSION_RESULT) {
                    param(FA.Param.Key.PERMISSION_TYPE, FA.Param.Value.EXACT_ALARM)
                    param(FA.Param.Key.STATUS, FA.Param.Value.DENIED)
                }
                dismissDialog()
            },
            onConfirm = {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.PERMISSION_RESULT) {
                    param(FA.Param.Key.PERMISSION_TYPE, FA.Param.Value.EXACT_ALARM)
                    param(FA.Param.Key.STATUS, FA.Param.Value.GRANTED)
                }
                navigateToExactAlarmRequest()
                dismissDialog()
            }
        )
    }

    if (homeUiState.deniedNotificationDialog) {
        PermissionDialog(
            titleId = R.string.feature_timer_permission_notification_title_request,
            contentTextId = R.string.feature_timer_permission_notification_message_request,
            dismissButtonTextId = R.string.feature_timer_permission_negative,
            confirmButtonTextId = R.string.feature_timer_permission_positive,
            onDismiss = {
                onShowErrorSnackBar(
                    Throwable(message = context.getString(R.string.feature_timer_permission_notification_denied))
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

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_UNDEFINED -> {
            HomeContentPortrait(
                orientation = configuration.orientation,
                paddingValues = paddingValues,
                homeUiState = homeUiState,
                hours = hours,
                minutes = minutes,
                initialHourIndex = initialHourIndex,
                initialMinuteIndex = initialMinuteIndex,
                hourState = hourState,
                minuteState = minuteState,
                unfocusedCount = unfocusedCount,
                navigateToNotificationPermissionSettings = navigateToNotificationPermissionSettings,
                navigateToSetting = navigateToSetting,
                startTimerAlarm = startTimerAlarm
            )
        }

        else -> { // Configuration.ORIENTATION_LANDSCAPE
            HomeContentLandscape(
                orientation = configuration.orientation,
                paddingValues = paddingValues,
                homeUiState = homeUiState,
                hours = hours,
                minutes = minutes,
                initialHourIndex = initialHourIndex,
                initialMinuteIndex = initialMinuteIndex,
                hourState = hourState,
                minuteState = minuteState,
                unfocusedCount = unfocusedCount,
                navigateToNotificationPermissionSettings = navigateToNotificationPermissionSettings,
                navigateToSetting = navigateToSetting,
                startTimerAlarm = startTimerAlarm
            )
        }
    }
}

@Composable
private fun HomeContentPortrait(
    orientation: Int,
    paddingValues: PaddingValues,
    homeUiState: HomeUiState,
    hours: ImmutableList<Int>,
    minutes: ImmutableList<Int>,
    initialHourIndex: Int,
    initialMinuteIndex: Int,
    hourState: LazyListState,
    minuteState: LazyListState,
    unfocusedCount: Int,
    navigateToNotificationPermissionSettings: () -> Unit,
    navigateToSetting: () -> Unit,
    startTimerAlarm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                        id = R.string.feature_timer_info_minute,
                        homeUiState.hour * 60 + homeUiState.minute
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary
                    ),
                )

                Text(
                    text = stringResource(
                        id = R.string.feature_timer_info_end_time,
                        homeUiState.estimatedEndTime
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                        fontWeight = FontWeight.Bold
                    ),
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
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textPrimary
                        )
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
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
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
                                navigateToNotificationPermissionSettings()
                            },
                        text = stringResource(
                            id = R.string.feature_timer_permission_notification_denied_text
                        ),
                        style = MaterialTheme.typography.labelSmall
                            .copy(color = EarAlarmMaterialTheme.colorScheme.textSecondary),
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
                            id = R.string.feature_timer_setting_alarm_volume,
                            homeUiState.volume
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textSecondary
                        ),
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = stringResource(
                            id = R.string.feature_timer_setting_alarm_media,
                            if (homeUiState.alarmMedia != null) {
                                homeUiState.alarmMedia.name
                            } else {
                                stringResource(id = R.string.feature_timer_setting_default_sound)
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall
                            .copy(color = EarAlarmMaterialTheme.colorScheme.textSecondary)
                    )
                }
            }
        }

        TimerControlButtons(
            orientation = orientation,
            homeUiState = homeUiState,
            hours = hours,
            minutes = minutes,
            initialHourIndex = initialHourIndex,
            initialMinuteIndex = initialMinuteIndex,
            hourState = hourState,
            minuteState = minuteState,
            unfocusedCount = unfocusedCount,
            startTimerAlarm = startTimerAlarm,
            navigateToSetting = navigateToSetting
        )
    }
}

@Composable
private fun HomeContentLandscape(
    orientation: Int,
    paddingValues: PaddingValues,
    homeUiState: HomeUiState,
    hours: ImmutableList<Int>,
    minutes: ImmutableList<Int>,
    initialHourIndex: Int,
    initialMinuteIndex: Int,
    hourState: LazyListState,
    minuteState: LazyListState,
    unfocusedCount: Int,
    navigateToNotificationPermissionSettings: () -> Unit,
    navigateToSetting: () -> Unit,
    startTimerAlarm: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
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
                        id = R.string.feature_timer_info_minute,
                        homeUiState.hour * 60 + homeUiState.minute
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary
                    ),
                )

                Text(
                    text = stringResource(
                        id = R.string.feature_timer_info_end_time,
                        homeUiState.estimatedEndTime
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                        fontWeight = FontWeight.Bold
                    ),
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
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textPrimary
                        )
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
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
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
                                navigateToNotificationPermissionSettings()
                            },
                        text = stringResource(
                            id = R.string.feature_timer_permission_notification_denied_text
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textSecondary
                        ),
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
                            id = R.string.feature_timer_setting_alarm_volume,
                            homeUiState.volume
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textSecondary
                        ),
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = stringResource(
                            id = R.string.feature_timer_setting_alarm_media,
                            if (homeUiState.alarmMedia != null) {
                                homeUiState.alarmMedia.name
                            } else {
                                stringResource(id = R.string.feature_timer_setting_default_sound)
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textSecondary
                        )
                    )
                }
            }
        }

        TimerControlButtons(
            modifier = Modifier.weight(1f),
            orientation = orientation,
            homeUiState = homeUiState,
            hours = hours,
            minutes = minutes,
            initialHourIndex = initialHourIndex,
            initialMinuteIndex = initialMinuteIndex,
            hourState = hourState,
            minuteState = minuteState,
            unfocusedCount = unfocusedCount,
            startTimerAlarm = startTimerAlarm,
            navigateToSetting = navigateToSetting
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 360,
    heightDp = 640,
    showBackground = true,
    locale = "ko"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 360,
    heightDp = 640,
    showBackground = true,
    locale = "ko"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 640,
    heightDp = 360,
    showBackground = true,
    locale = "ko"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 640,
    heightDp = 360,
    showBackground = true,
    locale = "ko"
)
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
            updateNotificationPermissionState = { _, _ -> },
            dismissDialog = {},
            startTimerAlarm = {},
            updateTimerAlarm = { _, _ -> }
        )
    }
}