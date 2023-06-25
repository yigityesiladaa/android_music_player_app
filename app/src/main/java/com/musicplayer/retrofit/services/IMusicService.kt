package com.musicplayer.retrofit.services

import retrofit2.http.GET
import com.musicplayer.models.MusicCategories
import retrofit2.Call

interface IMusicService {

    @GET("f27fbefc-d775-4aee-8d65-30f76f1f7109")
    fun getAll(): Call<MusicCategories?>

}