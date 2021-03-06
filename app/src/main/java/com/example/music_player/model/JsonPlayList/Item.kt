package com.example.music_player.model.JsonPlayList

data class Item(
    val artists: List<Artist>,
    val artists_names: String,
    val code: String,
    val content_owner: Int,
    val duration: Long = 0,
    val id: String,
    val isWorldWide: Boolean,
    val isoffical: Boolean,
    val link: String,
    val lyric: String,
    val mv_link: String,
    val name: String,
    val order: Any,
    val performer: String,
    val playlist_id: String,
    val thumbnail: String,
    val title: String,
    val type: String
)