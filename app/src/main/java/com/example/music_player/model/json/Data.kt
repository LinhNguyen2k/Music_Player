package com.example.music_player.model.json

data class Data(
    val customied: List<Any>,
    val peak_score: Int,
    val song: List<Song>,
    val songHis: SongHis
)