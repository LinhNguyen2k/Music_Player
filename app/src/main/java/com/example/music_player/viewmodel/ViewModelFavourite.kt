package com.example.music_player.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.music_player.model.json.Song
import com.example.music_player.repository.MusicSongRepository

class ViewModelFavourite( application: Application) : AndroidViewModel(application){
    var context: Context = getApplication<Application>().applicationContext
    var listMusicFavourite  =  MutableLiveData<ArrayList<Song>>()

      fun setLayoutFavouriteSongs(){
        listMusicFavourite.postValue(MusicSongRepository.getInstance().getListFavouriteSongs(context))
    }
}