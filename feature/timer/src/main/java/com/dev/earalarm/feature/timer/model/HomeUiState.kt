package com.dev.earalarm.feature.timer.model

import androidx.compose.runtime.Immutable
import java.io.File

@Immutable
data class HomeUiState(
    val hour: Int = 0,
    val minute: Int = 0,
    val estimatedEndTime: String = "AM 00:00",
    val volume: Int = 80,
    val alarmMedia: File? = null,
    val notificationPermissionState: PermissionState = PermissionState.GRANTED,
    val deniedExactAlarmDialog: Boolean = false,
    val deniedNotificationDialog: Boolean = false
)