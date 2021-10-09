package com.example.music_player

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.adapter.MusicAdapter
import com.example.music_player.model.Music
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicList: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        requestRunTimePermission()
        initViews()

    }

    private fun initViews() {

        MusicList = getAllAudio()
        rc_list_songs.setHasFixedSize(true)
        rc_list_songs.setItemViewCacheSize(13)
        rc_list_songs.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(MusicList, applicationContext)
        rc_list_songs.adapter = musicAdapter

        btn_shuffle.setOnClickListener {
            val intent = Intent(applicationContext, Player::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MusicAdapter")
            startActivity(intent)
        }
        btn_favorite.setOnClickListener {
            startActivity(Intent(applicationContext, Favorite_Player::class.java))
        }
        btn_playLists.setOnClickListener {
            startActivity(Intent(applicationContext, PlayList::class.java))
        }
    }

    private fun requestRunTimePermission() {
        if (ActivityCompat.checkSelfPermission(applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Đã được cho phép", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13)
            }
        }
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
            MediaStore.Audio.Media.DATE_ADDED,
            null)

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
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
                    val imgs = Uri.withAppendedPath(uri, albumC).toString()
                    val pathC =
                        cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))

                    val music = Music(id, title, album, artist, durationC, pathC, img = imgs)
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }

                } while (cursor.moveToNext())
            cursor.close()
        }

        return tempList
    }
}