package com.example.music_player.api

import com.example.music_player.model.JsonInfo.MusicInfo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiMusicInfo {
    // https://mp3.zing.vn/xhr/media/get-info?type=audio&id=ZW8I7AAI
    @GET("xhr/media/get-info")
    fun callAPI(@Query("type")type : String,
                @Query("id")id : String): Call<MusicInfo>
    companion object {

        val apiMusicInfo = Retrofit.Builder()
            .baseUrl("https://mp3.zing.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMusicInfo::class.java)
    }
}