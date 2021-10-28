package com.example.music_player.model.JsonInfo

data class MusicInfo(
    val `data`: Data,
    val err: Int,
    val msg: String,
    val timestamp: Long
)