package com.example.music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.JsonPlayList.MusicPlayList
import com.example.music_player.adapter.MusicListAdapter
import com.example.music_player.api.ApiMusicPlayList
import com.example.music_player.json.Song
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_play_list.*
import retrofit2.Call
import retrofit2.Response

class PlayList : AppCompatActivity() {
    private lateinit var musicPlayAdapter: MusicListAdapter

    companion object{
        var MusicListPlay  = ArrayList<Song>()
        var idSongs : String = "ZW67OIA0"
        var typeSongs : String = "audio"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        supportActionBar?.hide()
        initViews()
        img_back.setOnClickListener {
            finish()
        }
    }
     fun initViews() {
        //http://mp3.zing.vn/xhr/recommend?type=audio&id=ZW67OIA0
        idSongs = intent.getStringExtra("idSongs").toString()
        typeSongs = intent.getStringExtra("typeSongs").toString()
        ApiMusicPlayList.apiMusicPlayList.callAPI(typeSongs,idSongs).enqueue(object :
            retrofit2.Callback<MusicPlayList> {
            override fun onResponse(call: Call<MusicPlayList>, response: Response<MusicPlayList>) {
                MusicListPlay.clear()
                val root = response.body()!!
                MusicListPlay.addAll(root.data.items)
                rc_playList.setHasFixedSize(true)
                rc_playList.layoutManager = LinearLayoutManager(this@PlayList)
                musicPlayAdapter = MusicListAdapter(MusicListPlay, this@PlayList)
                rc_playList.adapter = musicPlayAdapter

            }

            override fun onFailure(call: Call<MusicPlayList>, t: Throwable) {
            }

        })
    }
}