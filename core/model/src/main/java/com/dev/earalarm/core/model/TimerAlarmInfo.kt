package com.dev.earalarm.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TimerAlarmInfo(
    val minute: Int,
    val startTime: String,
    val endTime: String
)