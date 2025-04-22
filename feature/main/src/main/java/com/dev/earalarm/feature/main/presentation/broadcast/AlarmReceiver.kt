package kr.ac.tukorea.android.earalarm.presentation.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import kr.ac.tukorea.android.earalarm.presentation.notification.NotificationHelper
import kr.ac.tukorea.android.earalarm.presentation.service.AlarmPlayingService
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreHelper.alarmInfo.first()?.let {
                        if (ZonedDateTime.parse(it.endTime) > ZonedDateTime.now()) {
                            alarmHelper.setTimerAlarm(it)
                        } else {
                            dataStoreHelper.deleteTimerAlarmInfo()
                        }
                    }
                }
            }

            AlarmHelper.INTENT_ACTION_TIMER_ALARM_ON -> {
                val serviceIntent = Intent(context, AlarmPlayingService::class.java).apply {
                    action = AlarmPlayingService.INTENT_ACTION_SERVICE_TIMER_ALARM_ON
                }
                context.startForegroundService(serviceIntent)
            }

            AlarmHelper.INTENT_ACTION_TIMER_ALARM_DISMISS -> {
                context.stopService(Intent(context, AlarmPlayingService::class.java))
            }
        }
    }
}