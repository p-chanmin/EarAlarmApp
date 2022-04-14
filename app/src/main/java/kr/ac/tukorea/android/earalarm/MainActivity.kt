package kr.ac.tukorea.android.earalarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var tPicker: TimePicker
    lateinit var endtimeText: TextView
    lateinit var endtimerText: TextView
    lateinit var add1hour: Button
    lateinit var add30min: Button
    lateinit var add10min: Button
    lateinit var add5min: Button
    lateinit var resetbtn: Button
    lateinit var settingbtn: Button
    lateinit var startTimerbtn: Button
    lateinit var alarmOnView: View
    lateinit var alarmOffView: View
    lateinit var alarmOffbtn: Button
    lateinit var alarmTextmin: TextView
    lateinit var alarmTextendtime: TextView
    lateinit var endtime: String
    lateinit var settingView : View
    lateinit var mediabtn : Button
    lateinit var alarm_media_text : TextView
    lateinit var alarm_volume_text : TextView
    lateinit var media_path : String
    lateinit var volume : String
    lateinit var volumeBeforeAlarm : String
    lateinit var volume_seek : SeekBar

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사용자 설정값 저장용
        prefs = PreferenceUtil(applicationContext)

        //연결
        tPicker = findViewById<TimePicker>(R.id.timePicker)
        endtimeText = findViewById<TextView>(R.id.endtimetext)
        endtimerText = findViewById<TextView>(R.id.timertext)
        add1hour = findViewById<Button>(R.id.add1hour)
        add30min = findViewById<Button>(R.id.add30min)
        add10min = findViewById<Button>(R.id.add10min)
        add5min = findViewById<Button>(R.id.add5min)
        resetbtn = findViewById<Button>(R.id.reset)
        settingbtn = findViewById<Button>(R.id.setting1)
        startTimerbtn = findViewById<Button>(R.id.startTimer)
        alarmOnView = findViewById<View>(R.id.tab_alarm_on)
        alarmOffView = findViewById<View>(R.id.tab_timer)
        alarmOffbtn = findViewById<Button>(R.id.alarm_off_btn)
        alarmTextmin = findViewById<TextView>(R.id.alarm_min)
        alarmTextendtime = findViewById<TextView>(R.id.alarm_endtime)
        alarm_media_text = findViewById<TextView>(R.id.alarm_media_text)
        alarm_volume_text = findViewById<TextView>(R.id.alarm_volume_text)

        // MainActivity 실행 시 현재 상태를 고려하여 뷰 설정
        when (prefs.getString("state", "alarm_off")) {
            "alarm_off" -> {    // 알람이 꺼져있을 경우 뷰 설정
                alarmOffView.visibility = View.VISIBLE
                alarmOnView.visibility = View.GONE
            }
            "alarm_on" -> { // 알람 실행 중 일 경우 뷰 설정 및 시간 가져오기
                alarmOffView.visibility = View.GONE
                alarmOnView.visibility = View.VISIBLE
                alarmTextmin.text = prefs.getString("alarmTextmin", "00분 알람")
                alarmTextendtime.text = prefs.getString("alarmTextendtime", "종료 시각 : 00:00")
            }
        }
        // 타임픽커 기본 설정
        // 시, 분 0으로 초기화 및 24시간 보기로 설정
        tPicker.currentHour = 0
        tPicker.currentMinute = 0
        tPicker.setIs24HourView(true)
        startTimerbtn.setClickable(false)

        // 오디오 매니저 설정
        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        
        // 알람 매니저, 알람 Intent 설정
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        // 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정
        val intentAlarm = Intent(this, AlarmReceiver::class.java)
        
        // 알람음과 알람볼륨 설정을 가져오고, 없다면 기본값을 가져옴
        media_path = prefs.getString("media_path", "defult")
        volume = prefs.getString("volume", "80")
        // 알람이 울리기 전 미디어 볼륨 가져옴
        volumeBeforeAlarm = prefs.getString("volumeBeforeAlarm", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString())
        
        // 인터페이스 반영
        alarm_media_text.text = "알람음 : " + prefs.getString("media_name", "기본음")
        alarm_volume_text.text = "알람볼륨 : " + prefs.getString("volume", "80")

        // 타임 픽커가 바뀌면 종료 시각 뷰 반영
        tPicker.setOnTimeChangedListener { timePicker, hour, min ->
            val currentTime: Long = System.currentTimeMillis()
            val date = Date(currentTime)
            val hourForm = SimpleDateFormat("HH")
            val minForm = SimpleDateFormat("mm")
            var hourTmp: Int = hourForm.format(date).toInt() + hour
            var minTmp: Int = minForm.format(date).toInt() + min
            var ampm: String
            // 종료 시각이 60분 이상일 경우
            if (minTmp >= 60) {
                hourTmp += 1
                minTmp = minTmp % 60
            }
            // 종료 시각이 24시 이상일 경우
            if (hourTmp >= 24) {
                hourTmp = hourTmp % 24
            }
            //am pm 전환
            if (hourTmp == 12) {
                ampm = "오후"
            } else if (hourTmp > 12) {
                ampm = "오후"
                hourTmp -= 12
            } else {
                ampm = "오전"
            }
            endtime = ampm + " " + String.format("%02d", hourTmp) + ":" +
                    String.format("%02d", minTmp)

            endtimeText.text = "종료 시각 : " + endtime

            endtimerText.text =
                Integer.toString(tPicker.getCurrentHour() * 60 + tPicker.getCurrentMinute()) + "분 후에 알람이 울립니다"

            // 타이머가 0시간 0분 일 경우 타이머 시작 버튼 비활성화
            if (tPicker.getCurrentHour() == 0 && tPicker.getCurrentMinute() == 0) {
                startTimerbtn.setClickable(false)
            } else {
                startTimerbtn.setClickable(true)
            }

        }

        // 시간 추가 , 리셋 버튼 이벤트
        add1hour.setOnClickListener {
            tPicker.currentHour = tPicker.getCurrentHour() + 1
        }
        add30min.setOnClickListener {
            if (tPicker.getCurrentMinute() + 30 >= 60) {
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 30) % 60
        }
        add10min.setOnClickListener {
            if (tPicker.getCurrentMinute() + 10 >= 60) {
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 10) % 60
        }
        add5min.setOnClickListener {
            if (tPicker.getCurrentMinute() + 5 >= 60) {
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 5) % 60
        }
        resetbtn.setOnClickListener {
            tPicker.currentHour = 0
            tPicker.currentMinute = 0
        }


        // 알람 설정 버튼 이벤트
        settingbtn.setOnClickListener {
            // 저장소 공간 허용
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Context.MODE_PRIVATE)
            
            // 다이어로그 생성
            var dlg = AlertDialog.Builder(this)
            dlg.setTitle("알람 설정")
            settingView = View.inflate(this, R.layout.dialog, null)
            dlg.setIcon(R.drawable.clock_with_earphone)
            dlg.setView(settingView)
            // 연결
            mediabtn = settingView.findViewById<Button>(R.id.setmedia)
            volume_seek = settingView.findViewById<SeekBar>(R.id.volume_seek)
            // 설정에서 현재 상태 반영
            mediabtn.text = "알람음 설정 : " + prefs.getString("media_name", "기본음")
            volume_seek.setProgress(Integer.parseInt(volume))
            // 알람음 변경 버튼 클릭 시
            mediabtn.setOnClickListener {
                var i = Intent(this, MyMusicPlayer::class.java)
                // 액티비티를 실행하면서 결과를 받아옴
                startActivityForResult(i, 0)
            }
            // 알람볼륨 변경 시
            volume_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                // seekBar의 상태가 변경 되었을 때
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    // 알람 볼륨 상태를 저장하고 뷰에 반영
                    prefs.setString("volume", p1.toString())
                    volume = p1.toString()
                    alarm_volume_text.text = "알람볼륨 : " + p1.toString()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
            // 다이어로그 설정 버튼 추가
            dlg.setPositiveButton("설정", null)
            // 다이어로그 출력
            dlg.show()
        }
        
        // 타이머 시작 버튼 이벤트
        startTimerbtn.setOnClickListener {
            // 알람이 울리기 전 미디어 볼륨 저장
            prefs.setString("volumeBeforeAlarm", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString())
            volumeBeforeAlarm = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString()

            // 알람 인텐트에 미디어 경로, 상태, 볼륨을 저장
            intentAlarm.putExtra("path", media_path)
            intentAlarm.putExtra("state", "alarm-on")
            intentAlarm.putExtra("volume", volume)

            // AlarmManager가 인텐트를 갖고 있다가 일정 시간이 흐른 뒤에 전달하기 때문에 PendingIntent로 만든다.
            var pendingIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.NOTIFICATION_ID, intentAlarm,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            // 현재 설정된 타이머를 분단위로 가지고 옴
            val sleepMinTime = tPicker.getCurrentHour() * 60 + tPicker.getCurrentMinute()

            val triggerTime = (SystemClock.elapsedRealtime()  // 분단위 -> 초단위 * 60 트리거 시간 실제 배포용
                    + sleepMinTime * 60 * 1000)
//            val triggerTime = (SystemClock.elapsedRealtime()  // 테스트용
//                    + sleepMinTime * 1000)

            // 알람매니저가 pendingIntent를 triggerTime 후에 보냄, 버전별로 실행을 다르게 함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            // 토스트 메세지 출력
            Toast.makeText(this, Integer.toString(sleepMinTime) + "분 후에 알람이 울립니다", Toast.LENGTH_SHORT).show()

            //레이아웃 설정
            alarmTextmin.text = sleepMinTime.toString() + "분 알람"
            alarmTextendtime.text = "종료 시각 " + endtime
            prefs.setString("state", "alarm_on")
            prefs.setString("alarmTextmin", alarmTextmin.text.toString())
            prefs.setString("alarmTextendtime", alarmTextendtime.text.toString())

            // 뷰 설정
            alarmOffView.visibility = View.GONE
            alarmOnView.visibility = View.VISIBLE
        }

        // 알람 해제 버튼 클릭
        alarmOffbtn.setOnClickListener {
            // 현재 상태를 알람 해제 상태로 저장
            prefs.setString("state", "alarm_off")

            // pendingIntent에 알람 off 상태를 저장해서 보냄
            intentAlarm.putExtra("state", "alarm-off")
            intentAlarm.putExtra("volume", volumeBeforeAlarm)
            var pendingIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.NOTIFICATION_ID, intentAlarm,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            // 알람 취소
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intentAlarm)

            // 알람 해제 후, 뷰 설정 및 초기화
            alarmOffView.visibility = View.VISIBLE
            alarmOnView.visibility = View.GONE
            resetbtn.callOnClick()

            // 토스트 메세지 출력
            Toast.makeText(this, "알람이 해제되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 설정에서 알람음을 변경 했을 때
        if(requestCode == 0){
            if(data?.hasExtra("name") == true){
                // data Intent에서 경로와 파일 이름을 받아옴
                var path : String = data?.getStringExtra("absolutePath").toString()
                var name : String = data?.getStringExtra("name").toString()
                // 레이아웃에 반영
                mediabtn.text = "알람음 설정 : " + name
                alarm_media_text.text = "알람음 : " + name
                // 설정 저장
                media_path = path
                prefs.setString("media_path", media_path)
                prefs.setString("media_name", name)

            }
        }
    }
}