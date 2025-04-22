package kr.ac.tukorea.android.earalarm.presentation.main.output

import java.io.File

data class AlarmUiState(
    val hour: Int = 0,
    val minute: Int = 0,
    val estimatedEndTime: String = "AM 00:00",
    val timerStartEnabled: Boolean = false,
    val volume: Int = 80,
    val alarmMedia: File? = null,
    val deniedExactAlarmDialog: Boolean = false,
    val deniedNotificationDialog: Boolean = false,
    val alarmSettingDialog: Boolean = false
)