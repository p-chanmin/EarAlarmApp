package kr.ac.tukorea.android.earalarm.presentation.main.model

import java.io.File
import java.time.ZonedDateTime

data class MainUiState(
    val hour: Int = 0,
    val minute: Int = 0,
    val estimatedEndTime: String = "AM 00:00",
    val timerStartEnabled: Boolean = false,
    val volume: Int = 80,
    val alarmMedia: File? = null
)

data class MeasuringUiState(
    val isMeasuring: Boolean = false,
    val minute: Int = 0,
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    val endTimeString: String = "AM 00:00",
    val progress: Int = 0,
    val leftTime: String = "00:00:00"
)