package com.example.music_player.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.music_player.model.json.Song
import com.example.music_player.repository.MusicSongRepository

class ViewModelTopSong (application: Application) : AndroidViewModel(application) {
    var context: Context = getApplication<Application>().applicationContext
    var listMusicOnline  =  MutableLiveData<ArrayList<Song>>()
    var listMusicSearch  =  MutableLiveData<ArrayList<com.example.music_player.model.JsonSearch.Song>>()

    init {
        setLayoutTopSong()

    }
    fun getAllTopSong() : MutableLiveData<ArrayList<Song>> = listMusicOnline
    fun getAllSearchSong() : MutableLiveData<ArrayList<com.example.music_player.model.JsonSearch.Song>> = listMusicSearch

    @SuppressLint("NullSafeMutableLiveData")
    private fun setLayoutTopSong(){
        MusicSongRepository.getInstance().getListTopSong { isSuccess, response ->
            if (isSuccess) {
                if(response!!.data.song.isEmpty()){
                    listMusicSearch.postValue(null)
                }else{
                    listMusicOnline.postValue(response!!.data.song as ArrayList<Song>?)                }

            } else {
                listMusicSearch.postValue(null)
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun setLayoutSearchSong(key : String){
        MusicSongRepository.getInstance().getSongSearch(key) { isSuccess, response ->
            if (isSuccess) {
                if(response!!.data.isEmpty()){
                    listMusicSearch.postValue(null)
                }else{
                    listMusicSearch.postValue(response!!.data[0].song as ArrayList<com.example.music_player.model.JsonSearch.Song>)
                }
            } else {
                listMusicSearch.postValue(null)
            }
        }
    }
}