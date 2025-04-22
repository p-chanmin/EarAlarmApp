package kr.ac.tukorea.android.earalarm.presentation.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import kr.ac.tukorea.android.earalarm.data.model.TimerAlarmInfo
import kr.ac.tukorea.android.earalarm.presentation.broadcast.AlarmReceiver
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmHelper @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
    private val context: Context
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
            Intent(context, AlarmReceiver::class.java).apply {
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
        dataStoreHelper.storeTimerAlarmInfo(timerAlarmInfo)
    }

    suspend fun cancelTimerAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TIMER_ALARM,
            Intent(context, AlarmReceiver::class.java).apply {
                action = INTENT_ACTION_TIMER_ALARM_ON
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        dataStoreHelper.deleteTimerAlarmInfo()
    }

    companion object {
        const val INTENT_ACTION_TIMER_ALARM_ON = "intentActionTimerAlarmOn"
        const val INTENT_ACTION_TIMER_ALARM_DISMISS = "intentActionTimerAlarmDismiss"

        const val REQUEST_CODE_TIMER_ALARM_DISMISS = 1
        const val REQUEST_CODE_TIMER_ALARM = 0
    }
}