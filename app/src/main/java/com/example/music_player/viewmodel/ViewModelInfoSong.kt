package com.example.music_player.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.music_player.repository.MusicSongRepository

class ViewModelInfoSong(application: Application) : AndroidViewModel(application) {
    var context: Context = getApplication<Application>().applicationContext
    var listInfoSong = MutableLiveData<String>()

    fun listInfo() : MutableLiveData<String> = listInfoSong
    fun getInfoSong(id : String, type : String){
        MusicSongRepository.getInstance().getListInfoSong(id, type) { isSuccess, response ->
            if (isSuccess) {
                when {
                    response!!.data.genres.isEmpty() -> {
                        listInfoSong.postValue(MutableLiveData<String>() as String)
                    }
                    response.data.genres.size == 1 -> {
                        listInfoSong.postValue(response!!.data.genres[0].name)
                    }
                    else -> {
                        listInfoSong.postValue(response!!.data.genres[1].name)
                    }
                }

            }
        }
    }
}