package com.example.music_player.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.music_player.api.ApiMusicInfo
import com.example.music_player.api.ApiMusicPlayList
import com.example.music_player.api.ApiService
import com.example.music_player.model.JsonInfo.MusicInfo
import com.example.music_player.model.JsonPlayList.MusicPlayList
import com.example.music_player.model.JsonSearch.MusicSearch
import com.example.music_player.model.Music
import com.example.music_player.model.json.Root
import retrofit2.Call
import retrofit2.Response
import java.io.File

class MusicSongRepository {
    companion object {
        private var INSTANCE: MusicSongRepository? = null
        fun getInstance() = INSTANCE
            ?: MusicSongRepository().also {
                INSTANCE = it
            }
    }
    //lay list nhac lien quan
         fun getListRecommendSong(typeSongs : String,idSongs : String, onResult: (isSuccess: Boolean, response: MusicPlayList?) -> Unit) {
        //http://mp3.zing.vn/xhr/recommend?type=audio&id=ZW67OIA0
        ApiMusicPlayList.apiMusicPlayList.callAPI(typeSongs,idSongs).enqueue(object :
            retrofit2.Callback<MusicPlayList> {
            override fun onResponse(call: Call<MusicPlayList>, response: Response<MusicPlayList>) {
                if (response.isSuccessful && response != null) {
                    onResult(true, response.body()!!)
                } else {
                    onResult(false, null)

                }

            }

            override fun onFailure(call: Call<MusicPlayList>, t: Throwable) {
                onResult(false, null)
            }

        })
    }
    //lay list nhac top 100
    fun getListTopSong(onResult: (isSuccess: Boolean, response: Root?) -> Unit){
        ApiService.apiService.callAPI(0,0,0,"song",false).enqueue(object :
            retrofit2.Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                if (response.isSuccessful && response != null) {
                    onResult(true, response.body()!!)
                } else {
                    onResult(false, null)

                }
            }
            override fun onFailure(call: Call<Root>, t: Throwable) {
                onResult(false, null)
            }

        })
    }
    //lay info song
    fun getListInfoSong(id : String, type : String ,onResult: (isSuccess: Boolean, response: MusicInfo?) -> Unit) {
        ApiMusicInfo.apiMusicInfo.callAPI(type,
            id)
            .enqueue(object : retrofit2.Callback<MusicInfo> {
                override fun onResponse(call: Call<MusicInfo>, response: Response<MusicInfo>) {
                    if (response.isSuccessful && response != null) {
                        onResult(true, response.body()!!)
                    } else {
                        onResult(false, null)

                    }
                }

                override fun onFailure(call: Call<MusicInfo>, t: Throwable) {
                    onResult(false, null)
                }
            })
    }
    //list search
    fun getSongSearch(
        keyWord: String,
        onResult: (isSuccess: Boolean, response: MusicSearch?) -> Unit
    ) {
        ApiService.apiSearch.callAPISearch("song","500",keyWord).enqueue(object :
            retrofit2.Callback<MusicSearch> {
            override fun onResponse(call: Call<MusicSearch>, response: Response<MusicSearch>) {
                if (response.isSuccessful && response != null) {
                    onResult(true, response.body()!!)
                } else {
                    onResult(false, null)

                }

            }

            override fun onFailure(call: Call<MusicSearch>, t: Throwable) {
                onResult(false, null)
            }


        })
    }
    //lay list nhac offline
    @SuppressLint("Range")
     fun getAllAudio(context: Context): ArrayList<Music> {
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
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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