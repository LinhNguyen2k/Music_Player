package com.example.music_player.json

import android.text.format.DateUtils
import com.example.music_player.FavoriteActivity
import com.example.music_player.OfflineActivity
import com.example.music_player.Player

data class Song(
    val album: Album,
    val artist: Artist,
    val artists: List<ArtistXX>,
    var artists_names: String,
    val code: String,
    val content_owner: Int,
    val duration: Long = 0,
    var id: String,
    val isWorldWide: Boolean,
    val isoffical: Boolean,
    val link: String,
    val lyric: String,
    val mv_link: String,
    var name: String,
    val order: Any,
    val performer: String,
    val playlist_id: String,
    val position: Int,
    val rank_num: Any,
    val rank_status: String,
    var thumbnail: String,
    var title: String,
    val total: Int,
    val type: String,
    val isCheck: Boolean = true,
) {
    constructor() : this(
        Album(), Artist(), emptyList(), "", "", -1, 0, "", false, false, "",
        "", "", "", "", "", "", -1, "", "", "", "", -1, "", true
    )
}

fun formatDuration(seconds: Long): String = if (seconds < 60) {
    seconds.toString()
} else {
    DateUtils.formatElapsedTime(seconds)
}

fun favoriteCheck(id: String): Int {
    Player.isFavorite = false
    FavoriteActivity.favoriteList.forEachIndexed { index, song ->
        if (song.isCheck){
            if (id == song.id) {
                Player.isFavorite = true
                return index
            }
        } else{
            Player.isFavorite = false
        }

    }
    return -1
}

fun downloadCheck(id: String): Int {
    Player.isDownload = false
    OfflineActivity.MusicList.forEachIndexed { index, music ->
        if (id == music.id) {
            Player.isDownload = true
            return index
        }
    }
    return -1

}