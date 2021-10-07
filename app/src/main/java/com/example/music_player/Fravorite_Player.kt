package com.example.music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Fravorite_Player : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fravorite_player)
        supportActionBar?.hide()
    }
}