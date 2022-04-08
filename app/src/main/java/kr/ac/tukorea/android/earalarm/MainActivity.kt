package kr.ac.tukorea.android.earalarm

import android.app.TabActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker

class MainActivity : TabActivity() {

    lateinit var tPicker : TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //연결
        tPicker = findViewById<TimePicker>(R.id.timePicker)

        // 탭 설정
        var tabHost = this.tabHost

        var tabSpecSong = tabHost.newTabSpec("TIMER").setIndicator("Timer")
        tabSpecSong.setContent(R.id.tab_timer)
        tabHost.addTab(tabSpecSong)

        var tabSpecArtist = tabHost.newTabSpec("ALARM").setIndicator("Alarm")
        tabSpecArtist.setContent(R.id.tab_alarm)
        tabHost.addTab(tabSpecArtist)

        tabHost.currentTab = 0

        // 타임픽커 기본 설정
        // 시, 분 0으로 초기화 및 24시간 보기로 설정
        tPicker.currentHour = 0
        tPicker.currentMinute = 0
        tPicker.setIs24HourView(true)


    }
}
