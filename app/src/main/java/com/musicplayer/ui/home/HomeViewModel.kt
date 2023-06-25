package com.musicplayer.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.musicplayer.common.utils.Utils
import com.musicplayer.R
import com.musicplayer.common.states.BaseState
import com.musicplayer.firebase.repositories.FirebaseAuthRepository
import com.musicplayer.firebase.repositories.FirebaseDBRepository
import com.musicplayer.models.Music
import com.musicplayer.models.MusicCategories
import com.musicplayer.models.MusicCategory
import com.musicplayer.retrofit.services.IMusicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class HomeViewModel : ViewModel() {
    var musicService: IMusicService? = null
    private val _firebaseDBRepository = FirebaseDBRepository()
    private var _firebaseAuthRepository = FirebaseAuthRepository()
    var categoryTitles = MutableLiveData<MutableList<String>>()
    private val _state by lazy { MutableLiveData<BaseState>(BaseState.Loading) }
    val state: LiveData<BaseState> = _state
    var musicList = MutableLiveData<HashMap<String, List<Music>>>()
    private var _context: Context? = null

    fun setContext(context: Context) {
        _context = context
    }

    fun getAll() {
        _firebaseDBRepository.getAll()?.addOnCompleteListener { task ->
            Utils.checkStatus<DataSnapshot>(_state, task) {
                val categoryCount = task.result.children.count()
                if (categoryCount > 0) {
                    task.result.children.forEach { ds ->
                        val result = ds.getValue(MusicCategory::class.java)
                        result?.let { setLists(it) }
                    }
                } else {
                    getFromApi()
                }
            }
        }
    }

    private fun getFromApi() {
        musicService?.getAll()?.enqueue(object : Callback<MusicCategories?> {
            override fun onResponse(call: Call<MusicCategories?>, response: Response<MusicCategories?>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    response?.musicCategories?.forEach { musicCategory ->
                        if (musicCategory.items.isNotEmpty()) {
                            setLists(musicCategory)
                            val fid = getFID()
                            if (fid != null) {
                                musicCategory.catId = fid
                                musicCategory.items.forEach { music ->
                                    music.mid = UUID.randomUUID().toString()
                                }
                            }
                            _firebaseDBRepository.insert(musicCategory)
                            _state.postValue(BaseState.Success())
                        }

                    }
                }
            }

            override fun onFailure(call: Call<MusicCategories?>, t: Throwable) {
                _state.postValue(BaseState.Error(t.localizedMessage))
            }

        })
    }

    private fun setLists(musicCategory: MusicCategory) {
        if (categoryTitles.value != null) {
            categoryTitles.value!!.add(musicCategory.baseTitle)
            if (musicList.value != null) {
                musicList.value!![musicCategory.baseTitle] = musicCategory.items
            } else {
                musicList.value = hashMapOf(musicCategory.baseTitle to musicCategory.items)
            }
        } else {
            categoryTitles.value = mutableListOf(musicCategory.baseTitle)
        }
    }

    fun signOut() {
        _firebaseAuthRepository.signOut()
        _state.postValue(BaseState.Success(_context?.getString(R.string.sign_out_text)))
    }

    fun getFID(): String? {
        return _firebaseDBRepository.getFID()
    }

}