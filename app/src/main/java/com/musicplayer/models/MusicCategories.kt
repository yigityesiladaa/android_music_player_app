package com.musicplayer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MusicCategories(
    val musicCategories: MutableList<MusicCategory> = mutableListOf()
)

@Parcelize
data class MusicCategory(
    var catId: String = "",
    var baseTitle: String = "",
    val items: List<Music> = listOf()
) : Parcelable

@Parcelize
data class Music(
    var mid: String = "",
    val baseCat: Long = 0,
    val title: String = "",
    val url: String = ""
) : Parcelable

