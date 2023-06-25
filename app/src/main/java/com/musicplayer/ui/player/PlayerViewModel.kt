package com.musicplayer.ui.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.musicplayer.R
import com.musicplayer.common.states.BaseState
import com.musicplayer.common.utils.Utils
import com.musicplayer.firebase.repositories.FirebaseDBRepository
import com.musicplayer.models.Music

class PlayerViewModel : ViewModel() {
    private val _firebaseDBRepository = FirebaseDBRepository()
    var isFavorite = MutableLiveData<Boolean>()
    private val _state by lazy { MutableLiveData<BaseState>(BaseState.Loading) }
    val state: LiveData<BaseState> = _state
    private var _context : Context? = null

    fun setContext(context : Context){
        _context = context
    }

    fun addToFavorite(music: Music){
        _firebaseDBRepository.insertToFavorites(music)?.addOnCompleteListener { task->
            Utils.checkStatus<Void>(_state,task,_context?.getString(R.string.add_to_favorites_success_text)){
                isFavorite.postValue(true)
            }
        }
    }

    fun removeFromFavorite(id : String){
        _firebaseDBRepository.removeFromFavorites(id)?.addOnCompleteListener { task->
            Utils.checkStatus<Void>(_state,task,_context?.getString(R.string.remove_from_favorites_success_text)){
                isFavorite.postValue(false)
            }
        }
    }

    fun isFavorite(id: String){
        _firebaseDBRepository.getFavoriteById(id)?.addOnCompleteListener { task->
            if(task.isSuccessful){
                if(task.result.childrenCount > 0){
                    isFavorite.postValue(true)
                }else{
                    isFavorite.postValue(false)
                }
            }
        }
    }


}