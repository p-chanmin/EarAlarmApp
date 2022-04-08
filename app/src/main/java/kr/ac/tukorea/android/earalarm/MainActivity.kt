package kr.ac.tukorea.android.earalarm

import android.app.TabActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.get
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : TabActivity() {

    lateinit var tPicker : TimePicker
    lateinit var endtimeText : TextView
    lateinit var add1hour : Button
    lateinit var add30min : Button
    lateinit var add10min : Button
    lateinit var add5min : Button
    lateinit var resetbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //연결
        tPicker = findViewById<TimePicker>(R.id.timePicker)
        endtimeText = findViewById<TextView>(R.id.endtimetext)
        add1hour = findViewById<Button>(R.id.add1hour)
        add30min = findViewById<Button>(R.id.add30min)
        add10min = findViewById<Button>(R.id.add10min)
        add5min = findViewById<Button>(R.id.add5min)
        resetbtn = findViewById<Button>(R.id.reset)

        // 탭 설정
        var tabHost = this.tabHost

        var tabSpecSong = tabHost.newTabSpec("TIMER").setIndicator("Timer")
        tabSpecSong.setContent(R.id.tab_timer)
        tabHost.addTab(tabSpecSong)

        var tabSpecArtist = tabHost.newTabSpec("ALARM").setIndicator("Alarm")
        tabSpecArtist.setContent(R.id.tab_alarm)
        tabHost.addTab(tabSpecArtist)

        tabHost.currentTab = 0

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

    }
}
