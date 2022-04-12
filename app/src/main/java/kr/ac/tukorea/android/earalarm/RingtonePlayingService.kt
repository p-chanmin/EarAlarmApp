package kr.ac.tukorea.android.earalarm


import android.app.AlertDialog
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.view.View


class RingtonePlayingService : Service() {
    companion object {
        const val TAG = "Ringtone"
        const val NOTIFICATION_ID = 1
        const val PRIMARY_CHANNEL_ID = "ringtone_channel"
    }

    var mediaPlayer: MediaPlayer? = null
    var startId = 0
    var isRunning = false

    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

//    override fun onCreate() {
//        notificationManager = this.getSystemService(
//            Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        createNotificationChannel()
//
//    }
//
//    fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                PRIMARY_CHANNEL_ID,
//                "Stand up notification",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.RED
//            notificationChannel.enableVibration(true)
//            notificationChannel.description = "AlarmManager Tests"
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }

    // 알람이 재생될 때 메인화면 띄우기
    fun alarmOnMain(){
        val main_intent = Intent(this, MainActivity::class.java)
        main_intent.putExtra("state", "alarm-on")
        startActivity(main_intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(AlarmReceiver.TAG, "@@@@@@@@@@@@@@시작한다")
        var startId : Int
        val getState = intent.getStringExtra("state")!!
        startId = when (getState) {
            "alarm-on" -> 1
            "alarm-off" -> 0
            else -> 0
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if (!isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.testsound)
            mediaPlayer!!.start()
            isRunning = true
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "@@@@@@@@@@@@@@미디어 재생")
            // alarmOnMain()

        } else if (isRunning && startId == 0) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            isRunning = false
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "@@@@@@@@@@@@@@2")
        } else if (!isRunning && startId == 0) {
            isRunning = false
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "@@@@@@@@@@@@@@3")
        } else if (isRunning && startId == 1) {
            isRunning = true
            this.startId = 1
            Log.d(AlarmReceiver.TAG, "@@@@@@@@@@@@@@4")
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestory() 실행", "서비스 파괴")
    }
}