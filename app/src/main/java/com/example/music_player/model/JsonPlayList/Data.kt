package com.example.music_player.model.JsonPlayList

import com.example.music_player.model.json.Song

data class Data(
    val image_url: String,
    val items: List<Song>,
    val total: Int
)