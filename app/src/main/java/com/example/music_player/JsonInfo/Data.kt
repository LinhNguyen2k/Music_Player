package com.example.music_player.JsonInfo

data class Data(
    val artists: List<Artist>,
    val composers: List<Any>,
    val contentOwner: ContentOwner,
    val genres: List<Genre>,
    val info: Info
)