package kr.ac.tukorea.android.earalarm.presentation.main.output

import java.time.ZonedDateTime

data class MeasuringUiState(
    val isMeasuring: Boolean = false,
    val minute: Int = 0,
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    val endTimeString: String = "AM 00:00",
    val progress: Float = 0f,
    val leftTime: String = "00:00:00"
)