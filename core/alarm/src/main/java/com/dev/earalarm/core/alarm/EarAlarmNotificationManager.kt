package com.dev.earalarm.core.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.dev.earalarm.core.navigation.DEEP_LINK_BASE_PATH
import javax.inject.Inject

class EarAlarmNotificationManager @Inject constructor(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createForegroundNotificationBuilder(): Notification {
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            EarAlarmManager.REQUEST_CODE_TIMER_ALARM,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(DEEP_LINK_BASE_PATH + "timer")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmDismissPendingIntent = PendingIntent.getBroadcast(
            context,
            EarAlarmManager.REQUEST_CODE_TIMER_ALARM_DISMISS,
            Intent(context, EarAlarmReceiver::class.java).apply {
                action = EarAlarmManager.INTENT_ACTION_TIMER_ALARM_DISMISS
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER_ALARM)
            .setSmallIcon(R.mipmap.ic_earalarm_launcher)
            .setContentTitle(context.getString(R.string.core_alarm_notification_title))
            .setContentText(context.getString(R.string.core_alarm_notification_content))
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_alarm_off_24,
                context.getString(R.string.core_alarm_dismiss_alarm),
                alarmDismissPendingIntent
            )
            .build()
    }

    fun registerNotificationChannels() {
        createNotificationChannel(
            R.string.core_alarm_notification_channel_timer_alarm_name,
            R.string.core_alarm_notification_channel_timer_alarm_description,
            CHANNEL_ID_TIMER_ALARM,
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    private fun createNotificationChannel(
        @StringRes notificationNameResId: Int,
        @StringRes notificationDescriptionResId: Int,
        channelId: String,
        importance: Int
    ) {
        val name = context.getString(notificationNameResId)
        val descriptionText = context.getString(notificationDescriptionResId)
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID_TIMER_ALARM = "channelIdTimerAlarm"
    }
}