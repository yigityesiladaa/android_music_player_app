package com.musicplayer.listeners

import com.musicplayer.models.Music

interface IHome {

    fun onListViewItemClick(music: Music)

    fun expandCollapseListView(position: Int)

}