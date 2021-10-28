package com.example.music_player.model.JsonInfo

data class Data(
    val artists: List<Artist>,
    val composers: List<Any>,
    val contentOwner: ContentOwner,
    val genres: List<Genre>,
    val info: Info
)