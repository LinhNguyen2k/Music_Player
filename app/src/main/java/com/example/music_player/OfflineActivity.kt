package com.example.music_player

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.adapter.MusicOfflineAdapter
import com.example.music_player.model.Music
import kotlinx.android.synthetic.main.activity_offline.*
import java.io.File

class OfflineActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicOfflineAdapter

    companion object {
        var MusicList = ArrayList<Music>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)
        supportActionBar?.hide()
        initViews()
        img_back.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        MusicList = getAllAudio()
        rc_list_song_download.setHasFixedSize(true)
        rc_list_song_download.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicOfflineAdapter(MusicList, applicationContext)
        rc_list_song_download.adapter = musicAdapter
    }

    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media._ID)))
                val title =
                    cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))
                val album =
                    cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))
                val artist =
                    cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                val durationC =
                    cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                val albumC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toString()
                val uri = Uri.parse("content://media/external/audio/albumart")
                val artUriC = Uri.withAppendedPath(uri, albumC).toString()
                val pathC =
                    cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))

                val music = Music(id, title, album, artist, durationC, pathC, artUri = artUriC,false)
                val file = File(music.path)
                if (file.exists()) {
                    tempList.add(music)
                }
            }

        }
        return tempList
    }
}