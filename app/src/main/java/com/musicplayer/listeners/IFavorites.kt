package com.musicplayer.listeners

import com.musicplayer.models.Music

interface IFavorites {

    fun onDeleteFromFavoritesClickListener(music: Music)

}