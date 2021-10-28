package com.example.music_player.model.json

data class ArtistX(
    val cover: String,
    val id: String,
    val link: String,
    val name: String,
    val thumbnail: String
){
    constructor() :this("","","","","")
}