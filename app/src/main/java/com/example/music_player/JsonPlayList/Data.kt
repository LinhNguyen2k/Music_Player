package com.example.music_player.JsonPlayList

import com.example.music_player.json.Song

data class Data(
    val image_url: String,
    val items: List<Song>,
    val total: Int
)