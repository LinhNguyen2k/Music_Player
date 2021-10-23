package com.example.music_player.JsonPlayList

data class MusicPlayList(
    val `data`: Data,
    val err: Int,
    val msg: String,
    val timestamp: Long
)