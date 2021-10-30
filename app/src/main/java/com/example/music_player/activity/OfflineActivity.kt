package com.example.music_player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.R
import com.example.music_player.adapter.MusicOfflineAdapter
import com.example.music_player.model.Music
import com.example.music_player.viewmodel.ViewModelListOffline
import kotlinx.android.synthetic.main.activity_offline.*

class OfflineActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicOfflineAdapter
    private lateinit var model : ViewModelListOffline
    companion object {
        var MusicList = ArrayList<Music>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)
        supportActionBar?.hide()
        initViews()
        img_back.setOnClickListener {
//            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        model = ViewModelProvider(this)[ViewModelListOffline::class.java]

        rc_list_song_download.setHasFixedSize(true)
        rc_list_song_download.layoutManager = LinearLayoutManager(this)
        model.getAllSongOffline().observe(this, Observer {
            MusicList = it
            musicAdapter = MusicOfflineAdapter(it, applicationContext)
            rc_list_song_download.adapter = musicAdapter
        })

    }

    override fun onBackPressed() {
//        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}