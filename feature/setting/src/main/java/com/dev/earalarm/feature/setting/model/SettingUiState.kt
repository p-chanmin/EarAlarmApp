package com.dev.earalarm.feature.setting.model

import java.io.File

data class SettingUiState(
    val volume: Int = 80,
    val alarmMedia: File? = null,
    val vibrate: Boolean = true,
)