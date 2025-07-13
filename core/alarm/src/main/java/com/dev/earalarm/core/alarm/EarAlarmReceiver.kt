package com.dev.earalarm.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dev.earalarm.core.data.TimerRepository
import com.dev.firebase.FirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class EarAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var timerRepository: TimerRepository

    @Inject
    lateinit var alarmHelper: EarAlarmManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val result = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        timerRepository.alarmInfo.first()?.let {
                            if (ZonedDateTime.parse(it.endTime) > ZonedDateTime.now()) {
                                alarmHelper.setTimerAlarm(it)
                            } else {
                                timerRepository.removeTimerAlarmInfo()
                            }
                        }
                    } finally {
                        result.finish()
                    }
                }
            }

            EarAlarmManager.INTENT_ACTION_TIMER_ALARM_ON -> {
                val serviceIntent = Intent(context, EarAlarmPlayingService::class.java).apply {
                    action = EarAlarmPlayingService.INTENT_ACTION_SERVICE_TIMER_ALARM_ON
                }
                context.startForegroundService(serviceIntent)
            }

            EarAlarmManager.INTENT_ACTION_TIMER_ALARM_DISMISS -> {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_DISMISS) {
                    param(FA.Param.Key.DISMISS_TYPE, FA.Param.Value.DISMISS_NOTIFICATION)
                }
                context.stopService(Intent(context, EarAlarmPlayingService::class.java))
            }
        }
    }
}