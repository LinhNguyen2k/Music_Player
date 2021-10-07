package com.example.music_player.model

data class Music(val id : String, val title : String, val album : String, val artist : String, val duration: Long = 0, val path : String)