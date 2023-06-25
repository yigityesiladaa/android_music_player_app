package com.musicplayer.firebase.services

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.musicplayer.models.Music

interface IFirebaseDBService<T> {

    fun getAll(): Task<DataSnapshot>?


    fun insert(data: T): Task<Void>?

    fun insertToFavorites(music: Music): Task<Void>?

    fun getAllFavorites(): Task<DataSnapshot>?

    fun getFavoriteById(id: String): Task<DataSnapshot>?

    fun removeFromFavorites(id: String): Task<Void>?

}