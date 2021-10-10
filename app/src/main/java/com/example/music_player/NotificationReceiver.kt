package com.example.music_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
        Player.binding.playerPause.setIconResource(R.drawable.ic_baseline_pause_24)
        NowPlayingFragment.binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_pause_24)

    }
    private fun pauseMusic(){
        Player.isPlaying = false
        Player.musicService!!.mediaPlayer!!.pause()
        Player.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        Player.binding.playerPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        NowPlayingFragment.binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)

    }
    private fun prevNextSong(increment : Boolean, context: Context){
        setSongPosition( check= increment)
        Player.musicService!!.createMusicPlayer()
        Glide.with(context)
            .load(Player.musicListPlayer[Player.songPosition].img)
            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
            .into(Player.binding.imgSongs)
        Glide.with(context)
            .load(Player.musicListPlayer[Player.songPosition].img)
            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
            .into(NowPlayingFragment.binding.imgSong)
        NowPlayingFragment.binding.tvSongNameFragment.text = Player.musicListPlayer[Player.songPosition].title
        Player.binding.tvSongName.text = Player.musicListPlayer[Player.songPosition].title
        playMusic()
    }
}