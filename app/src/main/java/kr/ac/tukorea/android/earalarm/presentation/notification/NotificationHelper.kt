package kr.ac.tukorea.android.earalarm.presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import kr.ac.tukorea.android.earalarm.presentation.broadcast.AlarmReceiver
import kr.ac.tukorea.android.earalarm.presentation.main.MainActivity
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createForegroundNotificationBuilder(): Notification {
        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            AlarmHelper.REQUEST_CODE_TIMER_ALARM,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmDismissPendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmHelper.REQUEST_CODE_TIMER_ALARM_DISMISS,
            Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmHelper.INTENT_ACTION_TIMER_ALARM_DISMISS
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER_ALARM)
            .setSmallIcon(R.mipmap.ic_earalarm_launcher)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_content))
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_alarm_off_24,
                context.getString(R.string.dismiss_alarm),
                alarmDismissPendingIntent
            ).build()
    }

    fun registerNotificationChannels() {
        createNotificationChannel(
            R.string.notification_channel_timer_alarm_name,
            R.string.notification_channel_timer_alarm_description,
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