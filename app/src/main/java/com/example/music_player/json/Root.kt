package com.example.music_player.json

data class Root(
    val `data`: Data,
    val err: Int,
    val msg: String,
    val timestamp: Long
)