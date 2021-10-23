package com.example.music_player.json

data class Artist(
    val link: String,
    val name: String
){
    constructor() : this ("", "")
}