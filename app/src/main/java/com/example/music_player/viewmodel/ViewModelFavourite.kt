package com.example.music_player.viewmodel

import androidx.lifecycle.ViewModel
import com.example.music_player.model.json.Song

class ViewModelFavourite : ViewModel() {
    companion object {
        var favoriteList : ArrayList<Song> = ArrayList()
    }

}