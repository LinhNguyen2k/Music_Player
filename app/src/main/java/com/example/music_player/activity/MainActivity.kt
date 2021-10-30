package com.example.music_player.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.R
import com.example.music_player.adapter.MusicAdapter
import com.example.music_player.adapter.MusicSearchAdapter
import com.example.music_player.model.json.Song
import com.example.music_player.viewmodel.ViewModelFavourite
import com.example.music_player.viewmodel.ViewModelTopSong
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicSearchAdapter: MusicSearchAdapter
    private lateinit var model : ViewModelTopSong
    private lateinit var modelFavourite : ViewModelFavourite
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
        getAllFavouriteSongs()
//        if (FavoriteActivity.favoriteList.size > 0){
//            setListFavouriteSongs()
//        }
//        FavoriteActivity.favoriteList = ArrayList()
//        val editor1 = getSharedPreferences("FAVORITE", MODE_PRIVATE)
//        val jsonString1 = editor1.getString("FavoriteSongs",null)
//        val typeToken = object : TypeToken<ArrayList<Song>>(){}.type
//        if (jsonString1 != null) {
//            val data: ArrayList<Song> = GsonBuilder().create().fromJson(jsonString1, typeToken)
//            FavoriteActivity.favoriteList.addAll(data)
//        }

        img_user.setOnClickListener {
            startActivity(Intent(applicationContext, FavoriteActivity::class.java))
        }
        img_download.setOnClickListener {
            startActivity(Intent(applicationContext, OfflineActivity::class.java))
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

    private fun getAllFavouriteSongs(){
        FavoriteActivity.favoriteList = ArrayList()
        modelFavourite = ViewModelProvider(this)[ViewModelFavourite::class.java]
        modelFavourite.setLayoutFavouriteSongs()
        modelFavourite.listMusicFavourite.observe(this, Observer {
            FavoriteActivity.favoriteList.addAll(it)
        })
    }
    private fun setListFavouriteSongs(){
        val editor = getSharedPreferences("FAVORITE", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favoriteList)
        editor.putString("FavoriteSongs",jsonString)
        editor.apply()
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
                if (it != null){
                    layout_notification.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    rc_list_songs_search.visibility = View.VISIBLE
                    layout_internet.visibility = View.GONE
                    rc_list_songs.visibility = View.GONE
                    listSearch.clear()
                    listSearch.addAll(it)
                    rc_list_songs_search.setHasFixedSize(true)
                    rc_list_songs_search.layoutManager = LinearLayoutManager(this@MainActivity)
                    musicSearchAdapter = MusicSearchAdapter(listSearch,this@MainActivity)
                    rc_list_songs_search.adapter = musicSearchAdapter
                    musicSearchAdapter.notifyDataSetChanged()
                }
                else {
                    layout_internet.visibility = View.VISIBLE
                    listSearch.clear()
                    musicSearchAdapter.notifyDataSetChanged()
                }

            })
        }
    }
    private fun initViews() {
        model = ViewModelProvider(this)[ViewModelTopSong::class.java]
        model.getAllTopSong().observe(this, Observer {
            if (it != null){
                MusicList.clear()
                MusicList = it
                layout_notification.visibility = View.GONE
                progressBar.visibility = View.GONE
                layout_internet.visibility = View.GONE
                rc_list_songs.setHasFixedSize(true)
                rc_list_songs.layoutManager = LinearLayoutManager(this@MainActivity)
                musicAdapter = MusicAdapter(it,this@MainActivity)
                rc_list_songs.adapter = musicAdapter
            } else {
                progressBar.visibility = View.VISIBLE
            }


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

