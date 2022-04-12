package kr.ac.tukorea.android.earalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var pendingIntent: PendingIntent
    lateinit var alarmTextmin: TextView
    lateinit var alarmTextendtime: TextView
    lateinit var endtime: String

    companion object {
        lateinit var prefs: PreferenceUtil
    }

//    // 바인더 연결
//    private lateinit var mService: RingtonePlayingService
//    private var mBound: Boolean = false
//    private val connection = object : ServiceConnection {
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            // RingtonePlayingService를 가져옴
//            val binder = service as RingtonePlayingService.LocalBinder
//            mService = binder.getService()
//            mBound = true
//        }
//
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            mBound = false
//        }
//    }
//    override fun onStart() {
//        super.onStart()
//        // Bind to LocalService
//        Intent(this, RingtonePlayingService::class.java).also { intent ->
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//        }
//    }
//    override fun onStop() {
//        super.onStop()
//        unbindService(connection)
//        mBound = false
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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


        // 타임 픽커가 바뀌면 종료 시각 반영
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

        // 타임픽커 기본 설정
        // 시, 분 0으로 초기화 및 24시간 보기로 설정
        tPicker.currentHour = 0
        tPicker.currentMinute = 0
        tPicker.setIs24HourView(true)

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

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intentAlarm =
            Intent(this, AlarmReceiver::class.java)  // 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정


        // 타이머 시작
        startTimerbtn.setOnClickListener {
            intentAlarm.putExtra("state", "alarm-on")
            // AlarmManager가 인텐트를 갖고 있다가 일정 시간이 흐른 뒤에 전달하기 때문에 PendingIntent로 만든다.
            pendingIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.NOTIFICATION_ID, intentAlarm,
                PendingIntent.FLAG_MUTABLE
            )
            // 현재 설정된 타이머를 분단위로 가지고 옴
            val sleepMinTime = tPicker.getCurrentHour() * 60 + tPicker.getCurrentMinute()

//            val triggerTime = (SystemClock.elapsedRealtime()  // 분단위 -> 초단위 * 60 트리거 시간 실제 배포용
//                    + sleepMinTime * 60 * 1000)
            val triggerTime = (SystemClock.elapsedRealtime()  // 테스트용
                    + sleepMinTime * 1000)

            // 버전별로 실행을 다르게 함
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
            Toast.makeText(
                this,
                Integer.toString(sleepMinTime) + "분 후에 알람이 울립니다",
                Toast.LENGTH_SHORT
            ).show()

            //레이아웃 설정
            alarmTextmin.text = sleepMinTime.toString() + "분 알람"
            alarmTextendtime.text = "종료 시각 " + endtime
            prefs.setString("state", "alarm_on")
            prefs.setString("alarmTextmin", alarmTextmin.text.toString())
            prefs.setString("alarmTextendtime", alarmTextendtime.text.toString())


            alarmOffView.visibility = View.GONE
            alarmOnView.visibility = View.VISIBLE

        }

        when (prefs.getString("state", "alarm_off")) {
            "alarm_off" -> {
                alarmOffView.visibility = View.VISIBLE
                alarmOnView.visibility = View.GONE
            }
            "alarm_on" -> {
                alarmOffView.visibility = View.GONE
                alarmOnView.visibility = View.VISIBLE
                alarmTextmin.text = prefs.getString("alarmTextmin", "00분 알람")
                alarmTextendtime.text = prefs.getString("alarmTextendtime", "종료 시각 : 00:00")
            }
        }

        // 알람 해제 버튼 클릭
        alarmOffbtn.setOnClickListener {
            prefs.setString("state", "alarm_off")

            intentAlarm.putExtra("state", "alarm-off")
            pendingIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.NOTIFICATION_ID, intentAlarm,
                PendingIntent.FLAG_MUTABLE
            )
            alarmManager.cancel(pendingIntent)
            intentAlarm.putExtra("state", "alarm-off")
            sendBroadcast(intentAlarm)

            alarmOffView.visibility = View.VISIBLE
            alarmOnView.visibility = View.GONE
            resetbtn.callOnClick()


        }
    }
}