package com.example.music_player.api

import com.example.music_player.JsonPlayList.MusicPlayList
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiMusicPlayList {
    //http://mp3.zing.vn/xhr/recommend?type=audio&id=ZW67OIA0
    @GET("xhr/recommend")
    fun callAPI(@Query("type")type : String,
                @Query("id")id : String): Call<MusicPlayList>
    companion object {

        val apiMusicPlayList = Retrofit.Builder()
            .baseUrl("https://mp3.zing.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMusicPlayList::class.java)
    }
}