package com.example.music_player.api

import com.example.music_player.JsonSearch.MusicSearch
import com.example.music_player.json.Root
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    //http://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1
    @GET("xhr/chart-realtime")
    fun callAPI(@Query("songId")songId : Int,
                @Query("videoId")videoId : Int,
                @Query("albumId")albumId : Int,
                @Query("chart")chart : String,
                @Query("time")time : Boolean): Call<Root>

    @GET("complete")
    fun callAPISearch(@Query("type") type: String,
                      @Query("num") num: String,
                      @Query("query") query: String): Call<MusicSearch>
    companion object {

        val apiService = Retrofit.Builder()
            .baseUrl("https://mp3.zing.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val apiSearch = Retrofit.Builder()
            .baseUrl("http://ac.mp3.zing.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}