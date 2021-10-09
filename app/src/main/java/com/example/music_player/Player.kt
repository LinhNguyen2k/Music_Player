package com.example.music_player

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player.databinding.ActivityPlayerBinding
import com.example.music_player.model.Music
import com.example.music_player.model.formatDuration
import com.example.music_player.model.setSongPosition
import kotlinx.android.synthetic.main.activity_player.*

class Player : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPlayer: ArrayList<Music>
        var songPosition = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat : Boolean = false
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
        btn_repeat.setOnClickListener {
            if (!repeat){
                repeat = true
                btn_repeat.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            }else {
                repeat = false
                btn_repeat.setColorFilter(ContextCompat.getColor(this, R.color.pink))
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2 ) musicService!!.mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })

    }

    private fun setLayout() {

        Glide.with(this)
            .load(musicListPlayer[songPosition].img)
            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
            .into(img_songs)
        if (repeat){
            btn_repeat.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
        }
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
            tv_startTime.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            tv_endTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            seekBar.progress = 0
            seekBar.max = musicService!!.mediaPlayer!!.duration
            playerPause.setIconResource(R.drawable.ic_baseline_pause_24)
            musicService!!.mediaPlayer!!.setOnCompletionListener (this)

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

    private fun playMusic() {
        playerPause.setIconResource(R.drawable.ic_baseline_pause_24)
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        isPlaying = true
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



    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMusicPlayer()
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(check = true)
        createMusicPlayer()
        try {
            setLayout()
        }catch (e : Exception){
            return
        }
    }

}