package kr.ac.tukorea.android.earalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = 0
        const val PRIMARY_CHANNEL_ID = "Alarm_channel"
    }

    private lateinit var context: Context
//    lateinit var state: String
//    lateinit var path: String

    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        Log.d(TAG, "Received intent : $intent")
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        var state = intent.getStringExtra("state")
        var path = intent.getStringExtra("path")
        Log.d(TAG, "알람 리시버에서 받을때@@@@@@@@ $path")
        Log.d(TAG, "알람 리시버에서 받을때@@@@@@@@  $state")

        // RingtonePlayingService 서비스 intent 생성
        val service_intent = Intent(context, RingtonePlayingService::class.java)

        // RingtonePlayinService로 extra string값 보내기
        service_intent.putExtra("state", state)
        service_intent.putExtra("path", path)

        // ringtone 서비스 시작
        this.context.startService(service_intent)

        // 오레오 버전 이상부터는 StartForegroundService를 사용해야한다고 하는데
        // StartForegroundService를 사용하면 오류 발생
        // StartService 사용하면 일단 재생됨
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            this.context.startForegroundService(service_intent)
//            Log.d(TAG, "StartForegroundService")
//        }else{
//            this.context.startService(service_intent)
//            Log.d(TAG, "StartService")
//        }
        
        // 알람이 on 상태일 때만 Notification 출력
        if(state == "alarm-on"){
            createNotificationChannel()
            deliverNotification(context)
        }
    }

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.earalarm_ic)
                .setContentTitle("알람이 울렸습니다")
                .setContentText("클릭하여 알람을 해제하세요.")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}