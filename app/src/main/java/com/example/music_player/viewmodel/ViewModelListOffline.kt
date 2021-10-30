package com.example.music_player.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.music_player.model.Music
import com.example.music_player.repository.MusicSongRepository

class ViewModelListOffline(application: Application) : AndroidViewModel(application) {
    var context: Context = getApplication<Application>().applicationContext
    var listMusicOffline  =  MutableLiveData<ArrayList<Music>>()
    init {
        setValues()
    }
        fun getAllSongOffline() : MutableLiveData<ArrayList<Music>> = listMusicOffline

    private fun setValues(){
        listMusicOffline.postValue(MusicSongRepository.getInstance().getAllAudio(context))
    }
}