package com.example.music_player

import android.app.DownloadManager
import android.content.*
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player.JsonInfo.MusicInfo
import com.example.music_player.api.ApiMusicInfo
import com.example.music_player.databinding.ActivityPlayerBinding
import com.example.music_player.json.*
import com.example.music_player.model.Music
import com.example.music_player.model.formatDurations
import com.example.music_player.model.getImage
import com.example.music_player.model.setSongPosition
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_player.*
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Method

class Player : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        var musicListPlayer = ArrayList<Song>()
        var musicListSearch = ArrayList<Song>()
        var listPhu = ArrayList<Song>()
        var musicListOffLine = ArrayList<Music>()
        var songPosition = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var repeatAll: Boolean = false
        var shuffle: Boolean = false
        var isFavorite: Boolean = false
        var favoriteIndex: Int = -1
        var downloadIndex: Int = -1
        var isChekOnline: Boolean = false
        var isDownload: Boolean = false
        var idDownload = 0L
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initLayout()
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
        btn_play.setOnClickListener {
            val intent = Intent(applicationContext,
                PlayList::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("idSongs", musicListPlayer[songPosition].id)
            intent.putExtra("typeSongs", musicListPlayer[songPosition].type)
            ContextCompat.startActivity(applicationContext, intent, null)
        }
        if (!isOnline(applicationContext)){
            btn_play.isEnabled = false
            btn_play.alpha = 0.4f
        }
        btn_shuffle.setOnClickListener {
            if (!shuffle) {
                shuffle = true
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList!!)
                musicListPlayer.shuffle()
                btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            } else {
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList!!)
                shuffle = false
                btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
            repeat = false
            btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.white))

            repeatAll = false
            btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }
        btn_repeatOne.setOnClickListener {
            if (!repeat) {
                repeat = true
                btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            } else {
                repeat = false
                btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
            repeatAll = false
            btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.white))

            shuffle = false
            btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }

        btn_repeatAll.setOnClickListener {
            if (!repeatAll) {
                repeatAll = true
                btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            } else {
                repeatAll = false
                btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
            repeat = false
            btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.white))

            shuffle = false
            btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) musicService!!.mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        img_more.setOnClickListener {
            showPopupMenu()
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun setLayout() {
//
        if (!isChekOnline) {
//            favoriteIndexOffline = favoriteCheckOffline(musicListOffLine[songPosition].id)
            val imageArt = getImage(musicListOffLine[songPosition].path)
            if (imageArt != null) {
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
            }
            binding.imgSongs.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                0,
                imageArt!!.size))
            if (repeat) {
                btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            }
            if (shuffle) {
                btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            }
            if (repeatAll) {
                btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
            }
            tv_songName.text = musicListOffLine[songPosition].artist
            tv_titleSong.text = musicListOffLine[songPosition].title
        }

            if (isChekOnline && musicListPlayer[songPosition].isCheck) {
                setLayoutTopList()
                favoriteIndex = favoriteCheck(musicListPlayer[songPosition].id)
                downloadIndex = downloadCheck(musicListPlayer[songPosition].id)
                val linkImg = musicListPlayer[songPosition].thumbnail.removeRange(34, 48)
                Picasso.with(this).load(linkImg).into(binding.imgSongs)
                if (repeat) {
                    btn_repeatOne.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
                }
                if (shuffle) {
                    btn_shuffle.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
                }
                if (repeatAll) {
                    btn_repeatAll.setColorFilter(ContextCompat.getColor(this, R.color.purple_700))
                }
                tv_songName.text = musicListPlayer[songPosition].artists_names
                tv_titleSong.text = musicListPlayer[songPosition].title
                when (intent.getStringExtra("class")) {
                    "MusicAdapter" -> {
                        setLayoutTopList()
                    }
                    "MusicListAdapter" -> {
                        setLayoutTopList()
                    }
                }
            }

    }

    private fun showPopupMenu() {
        val popup = PopupMenu(this, img_more)
        val menuOpts = popup.menu
        popup.apply {
            // inflate the popup menu
            menuInflater.inflate(R.menu.menu, menu)

            if (isFavorite) {
                menuOpts.getItem(0).setIcon(R.drawable.ic_baseline_favorite)
                menuOpts.getItem(0).title = "Xóa khỏi danh sách"
            } else {
                menuOpts.getItem(0).title = "Thêm vào danh sách"
                menuOpts.getItem(0).setIcon(R.drawable.ic_baseline_favorite_border_24)
            }
            // popup menu item click listener
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_favorite -> {
                        if (isFavorite) {
                            isFavorite = false
                                FavoriteActivity.favoriteList.removeAt(favoriteIndex)
                        } else {

                            if (isChekOnline && musicListPlayer[songPosition].isCheck) {
                                isFavorite = true
                                FavoriteActivity.favoriteList.add(0,musicListPlayer[songPosition])
                            } else {
                                isFavorite = false
                                val name = OfflineActivity.MusicList[songPosition].title
                                val id = OfflineActivity.MusicList[songPosition].id
                                val artist = OfflineActivity.MusicList[songPosition].artist
                                val duration = OfflineActivity.MusicList[songPosition].duration
                                val path = OfflineActivity.MusicList[songPosition].path
                                val img = OfflineActivity.MusicList[songPosition].artUri

                                FavoriteActivity.favoriteList.add(FavoriteActivity.favoriteList.size,Song(Album(),
                                    Artist(),
                                    emptyList(),
                                    artist,
                                    "",
                                    -1,
                                    duration,
                                    id,
                                    false,
                                    false,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    -1,
                                    "",
                                    path,
                                    img,
                                    name,
                                    -1,
                                    "",
                                    false))
                            }

                        }

                    }
                    R.id.item_download -> {

                        if (!isDownload) {
                            if ((isChekOnline)) {
                                Toast.makeText(applicationContext,
                                    "Đang tải xuống",
                                    Toast.LENGTH_SHORT).show()
                                val ur =
                                    "https://api.mp3.zing.vn/api/streaming/audio/${musicListPlayer[songPosition].id}/320"
                                val name = musicListPlayer[songPosition].name + ".mp3"
                                val request = DownloadManager.Request(Uri.parse(ur))
                                    .setTitle(name)
                                    .setDescription(musicListPlayer[songPosition].name)
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                        name)
                                val downloadManager =
                                    getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                idDownload = downloadManager.enqueue(request)
                                isDownload = true
                            } else {
                                Toast.makeText(applicationContext,
                                    "Bài hát đã có trong máy",
                                    Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            isDownload = true
                            Toast.makeText(applicationContext,
                                "Bài hát đã có trong máy",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                false
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        } else {
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popup]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // finally, show the popup menu
        popup.show()
    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Download complete
            var id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == idDownload) {
                Toast.makeText(applicationContext, "Tải xuống thành công", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun createMusicPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            if (isChekOnline) {
                musicService!!.mediaPlayer!!.setDataSource("http://api.mp3.zing.vn/api/streaming/audio/${musicListPlayer[songPosition].id}/320")
            } else if (!isChekOnline){
                musicService!!.mediaPlayer!!.setDataSource(musicListOffLine[songPosition].path)
            }
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            tv_startTime.text =
                formatDurations(musicService!!.mediaPlayer!!.currentPosition.toLong())
            tv_endTime.text = formatDurations(musicService!!.mediaPlayer!!.duration.toLong())
            seekBar.progress = 0
            seekBar.max = musicService!!.mediaPlayer!!.duration
            playerPause.setImageResource(R.drawable.ic_baseline_pause_24)
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

        } catch (e: Exception) {
            return
        }
    }

    private fun initLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPlayer = ArrayList()
                musicListPlayer.addAll(MainActivity.MusicList)
                setLayout()

            }
            "NowPlaying" -> {
                setLayout()
                tv_startTime.text =
                    formatDurations(musicService!!.mediaPlayer!!.currentPosition.toLong())
                tv_endTime.text = formatDurations(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            }
            "NowPlayingOffline" ->{
                setLayout()
                tv_startTime.text =
                    formatDurations(musicService!!.mediaPlayer!!.currentPosition.toLong())
                tv_endTime.text = formatDurations(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            }
            "MusicListAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPlayer = ArrayList()
                musicListPlayer.addAll((PlayList.MusicListPlay))
                setLayout()
            }
            "MusicSearchAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPlayer = ArrayList()
                musicListPlayer.addAll((musicListSearch))
                setLayout()
            }
            "MusicOfflineAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListOffLine = ArrayList()
                musicListOffLine.addAll(OfflineActivity.MusicList)

                setLayout()
            }
            "MusicFavoriteAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                if (FavoriteActivity.favoriteList[songPosition].isCheck){
                    musicListPlayer = ArrayList()
                    musicListPlayer.addAll((listPhu))
                    setLayout()
                }else {
                    musicListOffLine = ArrayList()
                    musicListOffLine.addAll(OfflineActivity.MusicList)
                    setLayout()
                }

            }
        }
    }

    private fun playMusic() {
        playerPause.setImageResource(R.drawable.ic_baseline_pause_24)
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        playerPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }
    private fun nextSongMusic(check: Boolean) {
        if (check) {
            setSongPosition(check = true)
            setLayout()
            createMusicPlayer()
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24)


        } else {
            setSongPosition(check = false)
            setLayout()
            createMusicPlayer()
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24)

        }
    }

    private fun setLayoutTopList() {
        if (isChekOnline) {
            ApiMusicInfo.apiMusicInfo.callAPI(musicListPlayer[songPosition].type,
                musicListPlayer[songPosition].id)
                .enqueue(object : retrofit2.Callback<MusicInfo> {
                    override fun onResponse(call: Call<MusicInfo>, response: Response<MusicInfo>) {
                        var root = response.body()!!
                        when {
                            root.data.genres.isEmpty() -> {
                                tv_typeSong.text = ""
                            }
                            root.data.genres.size == 1 -> {
                                tv_typeSong.text = root.data.genres[0].name
                            }
                            else -> {
                                tv_typeSong.text =
                                    root.data.genres[1].name
                            }
                        }
                    }

                    override fun onFailure(call: Call<MusicInfo>, t: Throwable) {
                    }
                })
        }

    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMusicPlayer()
        setLayoutTopList()
        musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(check = true)
        createMusicPlayer()
        if (isChekOnline) {
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
            Glide.with(applicationContext)
                .load(musicListPlayer[songPosition].thumbnail)
                .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
                .into(NowPlayingFragment.binding.imgSong)
            NowPlayingFragment.binding.tvSongNameFragment.text = musicListPlayer[songPosition].title
            setLayout()
        } else {
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
            val imageArt = getImage(musicListOffLine[songPosition].path)
            if (imageArt != null) {
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
            }
            binding.imgSongs.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                0,
                imageArt!!.size))
            NowPlayingFragment.binding.imgSong.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                0,
                imageArt!!.size))
            NowPlayingFragment.binding.tvSongNameFragment.text = musicListOffLine[songPosition].title
            setLayout()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}

