package com.example.music_player

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.JsonSearch.MusicSearch
import com.example.music_player.adapter.MusicAdapter
import com.example.music_player.adapter.MusicSearchAdapter
import com.example.music_player.api.ApiService
import com.example.music_player.json.Root
import com.example.music_player.json.Song
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicSearchAdapter: MusicSearchAdapter

    var tv_search = ""
    companion object{
          var MusicList  = ArrayList<Song>()
          var listSearch = ArrayList<com.example.music_player.JsonSearch.Song>()
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
        if (tv_search == "") {
            progressBar.visibility = View.GONE
            rc_list_songs_search.visibility = View.GONE
            rc_list_songs.visibility = View.VISIBLE
        } else {
            ApiService.apiSearch.callAPISearch("song","500",tv_search).enqueue(object :
                retrofit2.Callback<MusicSearch> {
                override fun onResponse(call: Call<MusicSearch>, response: Response<MusicSearch>) {
                    val responseBody = response.body()!!
                    layout_notification.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    rc_list_songs_search.visibility = View.VISIBLE
                    rc_list_songs.visibility = View.GONE
                    listSearch.clear()
                    if(responseBody.data.isNotEmpty()) {
                        listSearch.addAll(responseBody.data[0].song)

                    }
                    rc_list_songs_search.setHasFixedSize(true)
                    rc_list_songs_search.layoutManager = LinearLayoutManager(this@MainActivity)
                    musicSearchAdapter = MusicSearchAdapter(listSearch,this@MainActivity)
                    rc_list_songs_search.adapter = musicSearchAdapter
                    musicSearchAdapter.notifyDataSetChanged()

                }

                override fun onFailure(call: Call<MusicSearch>, t: Throwable) {

                }


            })
        }
    }
    private fun initViews() {

        ApiService.apiService.callAPI(0,0,0,"song",false).enqueue(object :
            retrofit2.Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                val root =response.body()!!
                layout_notification.visibility = View.GONE
                progressBar.visibility = View.GONE
                layout_internet.visibility = View.GONE
                MusicList.addAll(root.data.song)
                rc_list_songs.setHasFixedSize(true)
                rc_list_songs.layoutManager = LinearLayoutManager(this@MainActivity)
                musicAdapter = MusicAdapter(MusicList,this@MainActivity)
                rc_list_songs.adapter = musicAdapter
            }
            override fun onFailure(call: Call<Root>, t: Throwable) {
                layout_internet.visibility = View.VISIBLE
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

