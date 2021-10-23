package com.example.music_player

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.music_player.JsonInfo.MusicInfo
import com.example.music_player.api.ApiMusicInfo
import com.example.music_player.model.formatDurations
import com.example.music_player.model.getImage
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder() : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBnt: Int) {
        val prevIntent = Intent(baseContext,
            NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

//        val imageArt = getImage("https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/cover/4/9/d/a/49da6a1d6cf13a42e77bc3a945d9dd6b.jpg")
//        val image = if (imageArt != null){
//            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
//        }else{
//            BitmapFactory.decodeResource(resources, R.drawable.music_icons)
//        }
        suspend fun getBitmapFromURL(src: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(src)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    BitmapFactory.decodeStream(input)
                } catch (e: IOException) {
                    null
                }
            }
        }
        if (Player.isChekOnline){
            GlobalScope.launch(Dispatchers.Main) {
                var bitmap = getBitmapFromURL(Player.musicListPlayer[Player.songPosition].thumbnail)
                val exitIntent =
                    Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
                val exitPendingIntent = PendingIntent.getBroadcast(baseContext,
                    0,
                    exitIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                    .setContentTitle(Player.musicListPlayer[Player.songPosition].title)
                    .setContentText(Player.musicListPlayer[Player.songPosition].artists_names)
                    .setSmallIcon(R.mipmap.music_player)
                    .setLargeIcon(bitmap)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken))
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
        } else {

            val imageArt = getImage(Player.musicListOffLine[Player.songPosition].path)
            val image = if (imageArt != null){
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
            }else{
                BitmapFactory.decodeResource(resources, R.drawable.music_icons)
            }

            val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
            val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                .setContentTitle(Player.musicListOffLine[Player.songPosition].title)
                .setContentText(Player.musicListOffLine[Player.songPosition].artist)
                .setSmallIcon(R.mipmap.music_player)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.music_icons))
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(image)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_baseline_chevron_left_24, "", prevPendingIntent)
                .addAction(playPauseBnt, "", playPendingIntent)
                .addAction(R.drawable.ic_baseline_chevron_right_24, "", nextPendingIntent)
                .addAction(R.drawable.ic_baseline_exit_to_app_24, "", exitPendingIntent)
                .build()

            startForeground(13, notification)
        }



    }

    fun setLayoutTopList() {
        if (Player.isChekOnline) {
            ApiMusicInfo.apiMusicInfo.callAPI(Player.musicListPlayer[Player.songPosition].type,
                Player.musicListPlayer[Player.songPosition].id)
                .enqueue(object : retrofit2.Callback<MusicInfo> {
                    override fun onResponse(call: Call<MusicInfo>, response: Response<MusicInfo>) {
                        var root = response.body()!!
                        when {
                            root.data.genres.isEmpty() -> {
                                Player.binding.tvTypeSong.text = ""
                            }
                            root.data.genres.size == 1 -> {
                                Player.binding.tvTypeSong.text = root.data.genres[0].name
                            }
                            else -> {
                                Player.binding.tvTypeSong.text =
                                    root.data.genres[1].name
                            }
                        }
                    }

                    override fun onFailure(call: Call<MusicInfo>, t: Throwable) {
                    }
                })
        }

    }

    fun createMusicPlayer() {
        try {
            if (Player.musicService!!.mediaPlayer == null) Player.musicService!!.mediaPlayer =
                MediaPlayer()
            Player.musicService!!.mediaPlayer!!.reset()
            if (Player.isChekOnline && Player.musicListPlayer[Player.songPosition].isCheck) {
                Player.musicService!!.mediaPlayer!!.setDataSource("http://api.mp3.zing.vn/api/streaming/audio/${Player.musicListPlayer[Player.songPosition].id}/320")
            } else {
                Player.musicService!!.mediaPlayer!!.setDataSource(Player.musicListOffLine[Player.songPosition].path)
            }
            Player.musicService!!.mediaPlayer!!.prepare()
            Player.binding.playerPause.setImageResource(R.drawable.ic_baseline_pause_24)
            Player.binding.tvStartTime.text =
                formatDurations(Player.musicService!!.mediaPlayer!!.currentPosition.toLong())
            Player.binding.tvEndTime.text =
                formatDurations(Player.musicService!!.mediaPlayer!!.duration.toLong())
            Player.binding.seekBar.progress = 0
            Player.binding.seekBar.max = Player.musicService!!.mediaPlayer!!.duration
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            Player.binding.tvStartTime.text =
                formatDurations(Player.musicService!!.mediaPlayer!!.currentPosition.toLong())
            Player.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

}