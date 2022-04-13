package kr.ac.tukorea.android.earalarm

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import java.io.File
import java.io.FilenameFilter
import androidx.core.app.ActivityCompat


internal class Mp3Filter : FilenameFilter {
    override fun accept(dir: File?, name: String): Boolean {
        return name.endsWith(".mp3") // 확장자가 mp3인지 확인
    }
}

class MyMusicPlayer : ListActivity() {
    var MEDIA_PATH: String = "/sdcard/Download/"
    var listviews : ListView? = null

    var songs: MutableList<String> = ArrayList()
    public override fun onCreate(savedInstanceState: Bundle?) {
        try {

            Log.d("updateSongList : ", "CHECK2")
            super.onCreate(savedInstanceState)
            setContentView(R.layout.main)
            listviews = findViewById<ListView>(android.R.id.list)
            updateSongList()
        } catch (e: NullPointerException) {
            Log.v(getString(R.string.app_name), "NullPointerException") // 로그에 에러메시지 기록
        }

        listviews!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            var path = MEDIA_PATH + view.findViewById<TextView>(R.id.text1).text + "/"
            Log.d("updateSongList : ", "$view, i : $i, l : $l")
            var i = Intent(this, MainActivity::class.java)
            i.putExtra("absolutePath",MEDIA_PATH + view.findViewById<TextView>(R.id.text1).text)
            i.putExtra("name", view.findViewById<TextView>(R.id.text1).text)

            setResult(0, i)
            finish()
        }

    }

    fun updateSongList() {
        val musicList = ArrayAdapter(this@MyMusicPlayer, R.layout.music_item, songs)
        Log.d("updateSongList : ", MEDIA_PATH)
        val musicfiles = File(MEDIA_PATH)

        if (musicfiles.listFiles().size > 0) {
            for (file in musicfiles.listFiles()) {
                if (file.name.endsWith(".mp3")){
                    songs.add(file.name) // mp3파일을 ArrayList에 추가
                    Log.d("updateSongList : ", file.name)
                }

            }
            Log.d("updateSongList : ", songs.toString())

        }
        Log.d("updateSongList : ", songs.toString())
        if (listviews != null){
            listviews!!.adapter = musicList
            Log.d("updateSongList : ", "아답터 넣음")
        }
        else Log.d("updateSongList : ", "널이라고")
        Log.d("updateSongList : ", musicfiles.toString())


    }

}