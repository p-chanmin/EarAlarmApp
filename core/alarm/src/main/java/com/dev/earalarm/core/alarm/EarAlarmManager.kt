package com.dev.earalarm.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.core.model.TimerAlarmInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarAlarmManager @Inject constructor(
    private val timerRepository: TimerRepository,
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun checkScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    suspend fun setTimerAlarm(timerAlarmInfo: TimerAlarmInfo) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TIMER_ALARM,
            Intent(context, EarAlarmReceiver::class.java).apply {
                action = INTENT_ACTION_TIMER_ALARM_ON
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                ZonedDateTime.parse(timerAlarmInfo.endTime).toInstant().toEpochMilli(),
                pendingIntent
            ),
            pendingIntent
        )
        timerRepository.setTimerAlarmInfo(timerAlarmInfo)
    }

    suspend fun cancelTimerAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TIMER_ALARM,
            Intent(context, EarAlarmReceiver::class.java).apply {
                action = INTENT_ACTION_TIMER_ALARM_ON
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        context.stopService(Intent(context, EarAlarmPlayingService::class.java))
        timerRepository.removeTimerAlarmInfo()
    }

    companion object {
        const val INTENT_ACTION_TIMER_ALARM_ON = "intentActionTimerAlarmOn"
        const val INTENT_ACTION_TIMER_ALARM_DISMISS = "intentActionTimerAlarmDismiss"

        const val REQUEST_CODE_TIMER_ALARM_DISMISS = 1
        const val REQUEST_CODE_TIMER_ALARM = 0
    }
}