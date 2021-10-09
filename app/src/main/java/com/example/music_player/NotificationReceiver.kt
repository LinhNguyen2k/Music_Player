package com.example.music_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlin.system.exitProcess
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        when (p1?.action){
            ApplicationClass.PREVIOUS -> Toast.makeText(p0, "Previous Clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.PLAY -> {
                if (Player.isPlaying) pauseMusic()
                else playMusic()
            }
            ApplicationClass.NEXT -> Toast.makeText(p0, "Next Clicked", Toast.LENGTH_SHORT).show()
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
//        val share =  context.getSharedPreferences("playPause", Context.MODE_PRIVATE)
//        val editor = share.edit()
//        editor.putBoolean("check",  Player.isPlaying )
    }
    private fun pauseMusic(){
        Player.isPlaying = false
        Player.musicService!!.mediaPlayer!!.pause()
        Player.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        Player.binding.playerPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)
//        val share =  context.getSharedPreferences("playPause", Context.MODE_PRIVATE)
//        val editor = share.edit()
//        editor.putBoolean("check",  Player.isPlaying )
    }
}