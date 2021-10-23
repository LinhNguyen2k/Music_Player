package com.example.music_player.JsonInfo

data class MusicInfo(
    val `data`: Data,
    val err: Int,
    val msg: String,
    val timestamp: Long
)