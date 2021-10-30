package com.example.music_player.model.json

import android.text.format.DateUtils
import com.example.music_player.activity.FavoriteActivity
import com.example.music_player.activity.OfflineActivity
import com.example.music_player.activity.Player

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
fun favoriteCheckOffline(name: String): Int {
    Player.isFavorite = false
        for (i in 0 until FavoriteActivity.favoriteList.size){
            if (FavoriteActivity.favoriteList[i].artists_names == name ){
                Player.isFavorite = true
                return  i
            }
        }
    return -1
}
fun downloadCheck(name: String): Int {
    Player.isDownload = false
    if(Player.isChekOnline){
        for (i in 0 until OfflineActivity.MusicList.size){
            if (OfflineActivity.MusicList[i].artist == name ){
                Player.isDownload = true
                return  i
            }
        }
    }
    return -1

}