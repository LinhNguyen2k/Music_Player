package com.example.music_player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.music_player.model.Music

class Player : AppCompatActivity() {

    companion object{
        lateinit var musicListPlayer : ArrayList<Music>
        var songPosition = 0
        var mediaPlayer : MediaPlayer? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportActionBar?.hide()

        songPosition = intent.getIntExtra("index",0)
        when (intent.getStringExtra("class")){
            "MusicAdapter" -> {
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList)
                if (mediaPlayer == null) mediaPlayer = MediaPlayer()
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(musicListPlayer[songPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }
        }
    }
}