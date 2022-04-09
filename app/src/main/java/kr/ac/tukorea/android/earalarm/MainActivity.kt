package kr.ac.tukorea.android.earalarm

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var tPicker : TimePicker
    lateinit var endtimeText : TextView
    lateinit var endtimerText : TextView
    lateinit var add1hour : Button
    lateinit var add30min : Button
    lateinit var add10min : Button
    lateinit var add5min : Button
    lateinit var resetbtn : Button
    lateinit var settingbtn : Button
    lateinit var startTimerbtn : Button
    lateinit var alarmDialog : View

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // 타임 픽커가 바뀌면 종료 시각 반영
        tPicker.setOnTimeChangedListener { timePicker, hour, min ->
            val currentTime : Long = System.currentTimeMillis()
            val date = Date(currentTime)
            val hourForm = SimpleDateFormat("HH")
            val minForm = SimpleDateFormat("mm")
            val hourTime = hourForm.format(date)
            val minTime = minForm.format(date)
            var hourTmp : Int = hourForm.format(date).toInt() + hour
            var minTmp : Int = minForm.format(date).toInt() + min
            var ampm : String
            // 종료 시각이 60분 이상일 경우
            if (minTmp >= 60){
                hourTmp += 1
                minTmp = minTmp % 60
            }
            // 종료 시각이 24시 이상일 경우
            if (hourTmp >= 24){
                hourTmp = hourTmp % 24
            }
            //am pm 전환
            if (hourTmp == 12){
                ampm = "오후"
            }
            else if(hourTmp > 12){
                ampm = "오후"
                hourTmp -= 12
            }
            else{
                ampm = "오전"
            }
            endtimeText.text = "종료 시각 : " + ampm + " " + String.format("%02d", hourTmp) + ":" +
                    String.format("%02d", minTmp)

            endtimerText.text = Integer.toString(tPicker.getCurrentHour() * 60 + tPicker.getCurrentMinute())+"분 후에 알람이 울립니다"

            // 타이머가 0시간 0분 일 경우 타이머 시작 버튼 비활성화
            if (tPicker.getCurrentHour() == 0 && tPicker.getCurrentMinute() == 0) {
                startTimerbtn.setClickable(false)
            }
            else{
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
            if (tPicker.getCurrentMinute() + 30 >= 60){
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 30) % 60
        }
        add10min.setOnClickListener {
            if (tPicker.getCurrentMinute() + 10 >= 60){
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 10) % 60
        }
        add5min.setOnClickListener {
            if (tPicker.getCurrentMinute() + 5 >= 60){
                tPicker.currentHour += 1
            }
            tPicker.currentMinute = (tPicker.getCurrentMinute() + 5) % 60
        }
        resetbtn.setOnClickListener {
            tPicker.currentHour = 0
            tPicker.currentMinute = 0
        }



        settingbtn.setOnClickListener {

        }
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intentAlarm = Intent(this, AlarmReceiver::class.java)  // 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정

        var pendingIntent : PendingIntent

        // 타이머 시작
        startTimerbtn.setOnClickListener {

            intentAlarm.putExtra("state","alarm-on")
            // AlarmManager가 인텐트를 갖고 있다가 일정 시간이 흐른 뒤에 전달하기 때문에 PendingIntent로 만든다.
            pendingIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.NOTIFICATION_ID, intentAlarm,
                PendingIntent.FLAG_MUTABLE)

            // 현재 설정된 타이머를 분단위로 가지고 옴
            val sleepMinTime = tPicker.getCurrentHour() * 60 + tPicker.getCurrentMinute()

            val triggerTime = (SystemClock.elapsedRealtime()  // 분단위 -> 초단위 * 60 트리거 시간 설정해야함
                    + sleepMinTime * 1000)



            // 버전별로 실행을 다르게 함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            else{
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Toast.makeText(this, Integer.toString(sleepMinTime)+"분 후에 알람이 울립니다", Toast.LENGTH_SHORT).show()
        }

        // 알람 해제 수정 필요
        if (getIntent() != null && getIntent().getStringExtra("state") == "alarm-on"){
            alarmDialog = View.inflate(this@MainActivity, R.layout.dialog, null)
            var dlg = AlertDialog.Builder(this@MainActivity)
            dlg.setTitle("알람 해제")
            dlg.setIcon(R.drawable.clock_with_earphone)
            dlg.setView(alarmDialog)
            dlg.setPositiveButton("해제"){ alarmDialog, which ->
                // 알람 매니저 취소
                //alarmManager.cancel(pendingIntent)
                intentAlarm.putExtra("state", "alarm-off")
                sendBroadcast(intentAlarm)
            }
            dlg.show()
        }


    }
}
