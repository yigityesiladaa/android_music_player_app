package com.musicplayer.ui.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.musicplayer.R
import com.musicplayer.common.states.BaseState
import com.musicplayer.common.utils.Utils
import com.musicplayer.firebase.repositories.FirebaseDBRepository
import com.musicplayer.models.Music

class FavoritesViewModel : ViewModel() {
    private val _firebaseDBRepository = FirebaseDBRepository()
    var favorites = MutableLiveData<MutableList<Music>>()
    private val _state by lazy { MutableLiveData<BaseState>(BaseState.Loading) }
    val state: LiveData<BaseState> = _state
    private var _context: Context? = null

    fun setContext(context: Context) {
        _context = context
    }

    fun getAllFavorites() {
        _firebaseDBRepository.getAllFavorites()?.addOnCompleteListener { task ->
            Utils.checkStatus(_state, task) {
                favorites.value = mutableListOf()
                task.result.children.forEach { ds ->
                    val result = ds.getValue(Music::class.java)
                    result?.let {
                        if (favorites.value != null) {
                            favorites.value!!.add(0, it)
                        } else {
                            favorites.value = mutableListOf(it)
                        }
                    }
                }
            }
        }
    }

    fun removeFromFavorite(id: String) {
        _firebaseDBRepository.removeFromFavorites(id)?.addOnCompleteListener { task ->
            Utils.checkStatus<Void>(_state, task, _context?.getString(R.string.remove_from_favorites_success_text))
        }
    }
}