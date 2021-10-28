package com.example.music_player.model.json

data class Artist(
    val link: String,
    val name: String
){
    constructor() : this ("", "")
}