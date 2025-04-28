package com.dev.earalarm.feature.timer.model

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
data class MeasureUiState(
    val minute: Int = 0,
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    val endTimeString: String = "AM 00:00",
    val progress: Float = 0f,
    val leftTime: String = "00:00:00"
)