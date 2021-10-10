package com.example.music_player.model

import android.media.MediaMetadataRetriever
import com.example.music_player.Player
import java.util.concurrent.TimeUnit

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val img: String,
)

fun formatDuration(duration: Long): String {

    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)

}

fun getImage(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(check: Boolean) {
    if (!Player.repeat && !Player.repeatAll) {
        if (check) {
            if (Player.musicListPlayer.size - 1 == Player.songPosition) {
                Player.songPosition = 0
            } else {
                ++Player.songPosition
            }
        } else {
            if (0 == Player.songPosition) {
                Player.songPosition = Player.musicListPlayer.size - 1
            } else {
                --Player.songPosition
            }
        }
    } else if (Player.repeatAll && !Player.repeat) {
        if (Player.musicListPlayer.size - 1 == Player.songPosition) {
            Player.songPosition = 0
        } else {
            ++Player.songPosition
        }
    }

}