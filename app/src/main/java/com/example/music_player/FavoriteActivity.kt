package com.example.music_player

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.adapter.MusicFavoriteAdapter
import com.example.music_player.model.json.Song
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var musicAdapter : MusicFavoriteAdapter
    companion object {
        var favoriteList : ArrayList<Song> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        supportActionBar?.hide()
        img_back.setOnClickListener {
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }
        rc_list_song_favorite.setHasFixedSize(true)
        rc_list_song_favorite.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicFavoriteAdapter(favoriteList,this)
        rc_list_song_favorite.adapter = musicAdapter
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
    }
}