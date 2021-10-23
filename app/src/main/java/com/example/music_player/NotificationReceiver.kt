package com.example.music_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player.Player.Companion.musicListPlayer
import com.example.music_player.model.getImage
import com.example.music_player.model.setSongPosition
import kotlin.system.exitProcess
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        when (p1?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = p0!!)
            ApplicationClass.PLAY -> {
                if (Player.isPlaying) pauseMusic()
                else playMusic()
            }
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = p0!!)
            ApplicationClass.EXIT -> {
                Player.musicService!!.stopForeground(true)
                Player.musicService = null
                exitProcess(1)
            }

        }
    }
    private fun playMusic(){
        Player.isPlaying = true
        Player.musicService!!.mediaPlayer!!.start()
        Player.musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        Player.binding.playerPause.setImageResource(R.drawable.ic_baseline_pause_24)
        NowPlayingFragment.binding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)

    }
    private fun pauseMusic(){
        Player.isPlaying = false
        Player.musicService!!.mediaPlayer!!.pause()
        Player.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        Player.binding.playerPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        NowPlayingFragment.binding.btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)

    }
    private fun prevNextSong(increment : Boolean, context: Context){
        setSongPosition( check= increment)
        Player.musicService!!.createMusicPlayer()
        Player.musicService!!.setLayoutTopList()
        if (Player.isChekOnline ){
            val linkImg  = musicListPlayer[Player.songPosition].thumbnail.removeRange(34,48)
            Glide.with(context)
                .load(linkImg)
                .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
                .into(Player.binding.imgSongs)
            Glide.with(context)
                .load(linkImg)
                .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
                .into(NowPlayingFragment.binding.imgSong)
            NowPlayingFragment.binding.tvSongNameFragment.text = musicListPlayer[Player.songPosition].title
            Player.binding.tvSongName.text = musicListPlayer[Player.songPosition].title
            Player.binding.tvTitleSong.text = musicListPlayer[Player.songPosition].artists_names
            playMusic()
        } else {
            val imageArt = getImage(Player.musicListOffLine[Player.songPosition].path)
            if (imageArt != null) {
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
            }
            Player.binding.imgSongs.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                0,
                imageArt!!.size))
            NowPlayingFragment.binding.imgSong.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                0,
                imageArt!!.size))

            NowPlayingFragment.binding.tvSongNameFragment.text =
                Player.musicListOffLine[Player.songPosition].title
            Player.binding.tvSongName.text = Player.musicListOffLine[Player.songPosition].title
            Player.binding.tvTitleSong.text = Player.musicListOffLine[Player.songPosition].artist

            playMusic()
        }

    }
}