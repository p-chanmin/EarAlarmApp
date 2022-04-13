package kr.ac.tukorea.android.earalarm


import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log


class RingtonePlayingService : Service() {
    companion object {
        const val TAG = "Ringtone"
        const val NOTIFICATION_ID = 1
        const val PRIMARY_CHANNEL_ID = "ringtone_channel"
    }

    var mediaPlayer: MediaPlayer? = null
    var startId = 0
    var isRunning = false

    // 바인더 설정
    private val binder : IBinder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): RingtonePlayingService = this@RingtonePlayingService
    }

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
            mediaPlayer!!.isLooping = true
            isRunning = true
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "미디어 재생")
            //alarmOnMain()
        } else if (isRunning && startId == 0) { // 알람 울릴때 끄라고 했을 때
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            isRunning = false
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "알람 울리는 중 해제")
        } else if (!isRunning && startId == 0) {    // 안 울릴때 끄라고 했을 때
            isRunning = false
            this.startId = 0
            Log.d(AlarmReceiver.TAG, "알람 울리기 전 해제")
        }
        return START_NOT_STICKY
    }

    fun stopAlarm(){
        mediaPlayer!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestory() 실행", "서비스 파괴")
    }
}