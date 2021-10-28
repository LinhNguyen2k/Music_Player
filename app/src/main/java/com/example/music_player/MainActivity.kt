package com.example.music_player

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.adapter.MusicAdapter
import com.example.music_player.adapter.MusicSearchAdapter
import com.example.music_player.model.json.Song
import com.example.music_player.viewmodel.ViewModelTopSong
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicSearchAdapter: MusicSearchAdapter
    private lateinit var model : ViewModelTopSong
    var tv_search = ""
    companion object{
          var MusicList  = ArrayList<Song>()
          var listSearch = ArrayList<com.example.music_player.model.JsonSearch.Song>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initViews()


        FavoriteActivity.favoriteList = ArrayList()
        val editor = getSharedPreferences("FAVORITE", MODE_PRIVATE)
        val jsonString = editor.getString("FavoriteSongs",null)
        val typeToken = object : TypeToken<ArrayList<Song>>(){}.type
        if (jsonString != null){
            val data : ArrayList<Song> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavoriteActivity.favoriteList.addAll(data)
        }

        img_user.setOnClickListener {
            startActivity(Intent(applicationContext,FavoriteActivity::class.java))
        }
        img_download.setOnClickListener {
            startActivity(Intent(applicationContext,OfflineActivity::class.java))
        }
        edt_filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                tv_search = edt_filter.text.toString()
                searchSong()

            }
        })
    }

    fun searchSong(){
        model = ViewModelProvider(this)[ViewModelTopSong::class.java]
        if (tv_search == "") {
            progressBar.visibility = View.GONE
            rc_list_songs_search.visibility = View.GONE
            rc_list_songs.visibility = View.VISIBLE
        } else {
            model.setLayoutSearchSong(tv_search)
            model.getAllSearchSong().observe(this, Observer {
                layout_notification.visibility = View.GONE
                progressBar.visibility = View.GONE
                rc_list_songs_search.visibility = View.VISIBLE
                rc_list_songs.visibility = View.GONE
                listSearch.clear()
                listSearch.addAll(it)
                rc_list_songs_search.setHasFixedSize(true)
                rc_list_songs_search.layoutManager = LinearLayoutManager(this@MainActivity)
                musicSearchAdapter = MusicSearchAdapter(listSearch,this@MainActivity)
                rc_list_songs_search.adapter = musicSearchAdapter
            })
        }
    }
    private fun initViews() {
        model = ViewModelProvider(this)[ViewModelTopSong::class.java]

        model.getAllTopSong().observe(this, Observer {
            MusicList.clear()
            MusicList = it
            layout_notification.visibility = View.GONE
            progressBar.visibility = View.GONE
            layout_internet.visibility = View.GONE
            rc_list_songs.setHasFixedSize(true)
            rc_list_songs.layoutManager = LinearLayoutManager(this@MainActivity)
            musicAdapter = MusicAdapter(it,this@MainActivity)
            rc_list_songs.adapter = musicAdapter

        })
    }

    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVORITE", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favoriteList)
        editor.putString("FavoriteSongs",jsonString)
        editor.apply()

    }
}

