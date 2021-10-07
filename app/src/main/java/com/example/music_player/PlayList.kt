package com.example.music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlayList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        supportActionBar?.hide()
    }
}