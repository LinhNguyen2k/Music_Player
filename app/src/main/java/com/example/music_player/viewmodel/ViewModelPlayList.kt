package com.example.music_player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music_player.model.json.Song
import com.example.music_player.repository.MusicSongRepository

class ViewModelPlayList : ViewModel() {
    companion object {
        var listRecommendSong  =  MutableLiveData<ArrayList<Song>>()

    }

    fun getAllPlayList() : LiveData<ArrayList<Song>> = listRecommendSong

    fun setLayoutRecommendSong(type : String, id : String) {
        MusicSongRepository.getInstance().getListRecommendSong(type,id) { isSuccess, response ->
            if (isSuccess) {
                listRecommendSong.postValue(response!!.data.items as ArrayList<Song>)

            }
        }

    }

}