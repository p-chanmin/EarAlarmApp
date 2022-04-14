package kr.ac.tukorea.android.earalarm


import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log


class RingtonePlayingService : Service() {
    var mediaPlayer: MediaPlayer? = null
    var isRunning = false
    lateinit var path : String

    // 바인더 설정
    private val binder : IBinder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
    inner class LocalBinder : Binder() {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // alarmReceiver 에서 state와 volume 정보를 저장
        val getState = intent.getStringExtra("state")!!
        val getVolume = intent.getStringExtra("volume")!!

        // 오디오 매니저 설정
        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // 알람 울리지 않을 때 알람을 키라고 했을 때
        if (!isRunning && getState == "alarm-on") {
            // 알람 볼륨 설정
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*(0.01 * getVolume.toInt())).toInt(),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            // 미디어 플레이어 초기화
            mediaPlayer = MediaPlayer()
            // alarmReceiver 에서 path 정보를 저장
            path = intent.getStringExtra("path")!!
            // path가 defult인 경우와 절대경로인 경우
            if (path == "defult"){  // defult인 경우 samplesound 저장
                mediaPlayer = MediaPlayer.create(this, R.raw.samplesound)
            }
            else{   // 절대경로인 경우 해당 경로의 미디어 저장
                mediaPlayer!!.setDataSource(path)
                mediaPlayer!!.prepare()
            }
            // 미디어 재생
            mediaPlayer!!.start()
            // 반복재생 ON
            mediaPlayer!!.isLooping = true
            // Running 상태 변경
            isRunning = true
        }
        else if (isRunning && getState == "alarm-off") { // 알람 울릴 때 끄라고 했을 때
            // 볼륨을 전달받아 알람 울리기 이전 상태의 미디어 볼륨으로 설정
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (getVolume).toInt(),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            // 알람 정지
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            isRunning = false
        }
        else if (!isRunning && getState == "alarm-off") {    // 안 울릴때 끄라고 했을 때
            // 알람 정지 상태 반영
            isRunning = false
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestory() 실행", "서비스 파괴")
    }
}