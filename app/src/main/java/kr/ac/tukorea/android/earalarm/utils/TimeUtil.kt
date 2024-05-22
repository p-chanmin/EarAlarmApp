package kr.ac.tukorea.android.earalarm.utils

import java.time.Duration
import java.time.ZonedDateTime

fun ZonedDateTime.getRemainingTimeFromNow(): String {
    val currentTime = ZonedDateTime.now()

    val duration = Duration.between(currentTime, this).seconds

    return if (duration < 0) "00:00:00" else String.format(
        "%02d:%02d:%02d",
        duration / 3600,
        (duration % 3600) / 60,
        duration % 60
    )
}

fun getTimerProgressFromNow(startTime: ZonedDateTime, endTime: ZonedDateTime): Int {
    val totalTime = Duration.between(startTime, endTime).toMillis().toDouble()
    val measuredTime = Duration.between(startTime, ZonedDateTime.now()).toMillis().toDouble()
    val percent = (measuredTime / totalTime) * 100
    return if (percent < 0) 0 else if (percent > 100) 100 else percent.toInt()
}