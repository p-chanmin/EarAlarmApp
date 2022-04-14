package kr.ac.tukorea.android.earalarm

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.File


class MyMusicPlayer : ListActivity() {
    // mp3 파일이 저장될 디렉토리 경로
    var MEDIA_PATH: String = "/sdcard/Download/"
    var listviews : ListView? = null

    var songs: MutableList<String> = ArrayList()
    
    public override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.main)
            // listview 연결
            listviews = findViewById<ListView>(android.R.id.list)
            // listview 업데이트
            updateSongList()
        } catch (e: NullPointerException) {
            Log.d(getString(R.string.app_name), "NullPointerException") // 로그에 에러메시지 기록
        }
        // listview의 아이템이 클릭 되었을 때
        listviews!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            // Intent 생성
            var i = Intent(this, MainActivity::class.java)
            // extra에 절대 경로와 파일 이름 저장
            i.putExtra("absolutePath",MEDIA_PATH + view.findViewById<TextView>(R.id.text1).text)
            i.putExtra("name", view.findViewById<TextView>(R.id.text1).text)
            // 결과를 보내고 액티비티 종료
            setResult(0, i)
            finish()
        }

    }
    // listview 업데이트
    fun updateSongList() {
        // songs 리스트를 사용한 ArrayAdapter 생성
        val musicList = ArrayAdapter(this@MyMusicPlayer, R.layout.music_item, songs)
        
        // MEDIA_PATH 오픈
        val musicfiles = File(MEDIA_PATH)
        
        // 파일리스트 중에 mp3 파일만 songs 리스트에 추가
        if (musicfiles.listFiles().size > 0) {
            for (file in musicfiles.listFiles()) {
                if (file.name.endsWith(".mp3")){
                    songs.add(file.name) // mp3파일을 ArrayList에 추가
                }
            }
        }
        // adapter에 반영하면서 listviews 업데이트
        if (listviews != null){
            listviews!!.adapter = musicList
        }

    }

}