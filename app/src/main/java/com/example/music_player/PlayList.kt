package com.example.music_player

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.adapter.MusicListAdapter
import com.example.music_player.model.json.Song
import com.example.music_player.viewmodel.ViewModelPlayList
import kotlinx.android.synthetic.main.activity_play_list.*

class PlayList : AppCompatActivity() {
    private lateinit var musicPlayAdapter: MusicListAdapter
    private lateinit var model: ViewModelPlayList
    companion object {
        var MusicListPlay = ArrayList<Song>()
        var idSongs: String = "ZW67OIA0"
        var typeSongs: String = "audio"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        supportActionBar?.hide()
        initViews()
        img_back.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }


    fun initViews() {
        idSongs = intent.getStringExtra("idSongs").toString()
        typeSongs = intent.getStringExtra("typeSongs").toString()
        rc_playList.setHasFixedSize(true)
        rc_playList.layoutManager = LinearLayoutManager(this@PlayList)
        model = ViewModelProvider(this)[ViewModelPlayList::class.java]
        model.setLayoutRecommendSong(typeSongs, idSongs)
        model.getAllPlayList().observe(this, Observer {
            MusicListPlay.clear()
            MusicListPlay.addAll(it)
            musicPlayAdapter = MusicListAdapter(it, applicationContext)
            rc_playList.adapter = musicPlayAdapter
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

}