package com.example.music_player

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player.databinding.ActivityPlayerBinding
import com.example.music_player.model.Music
import kotlinx.android.synthetic.main.activity_player.*

class Player : AppCompatActivity(), ServiceConnection {

    companion object {
        lateinit var musicListPlayer: ArrayList<Music>
        var songPosition = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initLayout()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        playerPause.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }
        img_back.setOnClickListener {
            finish()
        }
        btn_nextLeft.setOnClickListener {
            nextSongMusic(false)
        }
        btn_nextRight.setOnClickListener {
            nextSongMusic(true)
        }

    }

    private fun setLayout() {

        Glide.with(this)
            .load(musicListPlayer[songPosition].img)
            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
            .into(img_songs)

        tv_songName.text = musicListPlayer[songPosition].title

    }

    private fun createMusicPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPlayer[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            playerPause.setIconResource(R.drawable.ic_baseline_pause_24)

        } catch (e: Exception) {
            return
        }
    }

    private fun initLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList)
                setLayout()

            }
            "MainActivity" -> {
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList)
                musicListPlayer.shuffle()
                setLayout()
                createMusicPlayer()
            }
        }
    }
     fun pausePlayBnt(pausePlayBnt: Int){
        playerPause.setIconResource(pausePlayBnt)
    }

    private fun playMusic() {
        playerPause.setIconResource(R.drawable.ic_baseline_pause_24)
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        isPlaying = true
//        val share =  this.getSharedPreferences("playPause", Context.MODE_PRIVATE)
//        var check = share.getBoolean("check", false)
//        if (check){
//            playerPause
//        }
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        playerPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun nextSongMusic(check: Boolean) {
        if (check) {
            setSongPosition(check = true)
            setLayout()
            createMusicPlayer()
        } else {
            setSongPosition(check = false)
            setLayout()
            createMusicPlayer()
        }
    }

    private fun setSongPosition(check: Boolean) {
        if (check) {
            if (musicListPlayer.size - 1 == songPosition) {
                songPosition = 0
            } else {
                ++songPosition
            }
        } else {
            if (0 == songPosition) {
                songPosition = musicListPlayer.size - 1
            } else {
                --songPosition
            }
        }

    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMusicPlayer()
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

}