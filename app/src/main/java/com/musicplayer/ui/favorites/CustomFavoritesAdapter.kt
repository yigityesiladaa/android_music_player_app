package com.musicplayer.ui.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.musicplayer.R
import com.musicplayer.listeners.IFavorites
import com.musicplayer.models.Music

class CustomFavoritesAdapter(private val context: Context, private val listener: IFavorites) :
    ArrayAdapter<Music>(context, R.layout.favorites_list_item) {

    private var favoriteMusics = mutableListOf<Music>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.favorites_list_item, null, true)

        val txtFavoriteMusicTitle = rootView.findViewById<TextView>(R.id.txtFavoriteTitle)
        val btnDeleteFromFavorites = rootView.findViewById<ImageButton>(R.id.btnRemoveFromFavorites)

        val music = favoriteMusics[position]

        txtFavoriteMusicTitle.text = music.title
        btnDeleteFromFavorites.setOnClickListener {
            listener.onDeleteFromFavoritesClickListener(music)
        }

        return rootView
    }

    override fun getCount(): Int {
        return favoriteMusics.count()
    }

    fun submitList(list: MutableList<Music>) {
        favoriteMusics = list
        notifyDataSetChanged()
    }
}