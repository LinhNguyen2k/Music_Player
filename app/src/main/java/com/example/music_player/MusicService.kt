package com.example.music_player

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.music_player.model.formatDuration
import com.example.music_player.model.getImage
import kotlinx.android.synthetic.main.activity_player.*

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer : MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }
    inner class MyBinder() : Binder(){
        fun currentService() : MusicService{
            return this@MusicService
        }
    }
    fun showNotification(playPauseBnt : Int){
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val imageArt = getImage(Player.musicListPlayer[Player.songPosition].path)
        val image = if (imageArt != null){
            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_icons)
        }

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(Player.musicListPlayer[Player.songPosition].title)
            .setContentText(Player.musicListPlayer[Player.songPosition].artist)
            .setSmallIcon(R.mipmap.music_player)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_baseline_chevron_left_24, "", prevPendingIntent)
            .addAction(playPauseBnt, "", playPendingIntent)
            .addAction(R.drawable.ic_baseline_chevron_right_24, "", nextPendingIntent)
            .addAction(R.drawable.ic_baseline_exit_to_app_24, "", exitPendingIntent)
            .build()

        startForeground(13, notification)

    }
     fun createMusicPlayer() {
        try {
            if (Player.musicService!!.mediaPlayer == null) Player.musicService!!.mediaPlayer = MediaPlayer()
            Player.musicService!!.mediaPlayer!!.reset()
            Player.musicService!!.mediaPlayer!!.setDataSource(Player.musicListPlayer[Player.songPosition].path)
            Player.musicService!!.mediaPlayer!!.prepare()
            Player.binding.playerPause.setIconResource(R.drawable.ic_baseline_pause_24)
            Player.binding.tvStartTime.text = formatDuration(Player.musicService!!.mediaPlayer!!.currentPosition.toLong())
            Player.binding.tvEndTime.text = formatDuration(Player.musicService!!.mediaPlayer!!.duration.toLong())
            Player.binding.seekBar.progress = 0
            Player.binding.seekBar.max = Player.musicService!!.mediaPlayer!!.duration
        } catch (e: Exception) {
            return
        }
    }
    fun seekBarSetup(){
        runnable = Runnable {
            Player.binding.tvStartTime.text = formatDuration(Player.musicService!!.mediaPlayer!!.currentPosition.toLong())
            Player.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }









}