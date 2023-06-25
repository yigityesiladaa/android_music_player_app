package com.musicplayer.ui.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.musicplayer.R
import com.musicplayer.common.states.BaseState
import com.musicplayer.common.utils.Utils
import com.musicplayer.firebase.repositories.FirebaseAuthRepository

class AuthViewModel : ViewModel() {

    private var _firebaseAuthRepository = FirebaseAuthRepository()
    private val _state by lazy { MutableLiveData<BaseState>(BaseState.Loading) }
    val state: LiveData<BaseState> = _state
    private var _context : Context? = null

    fun setContext(context : Context){
        _context = context
    }

    fun signIn(email : String, password : String){
        _firebaseAuthRepository.signIn(email,password).addOnCompleteListener { task->
            Utils.checkStatus<AuthResult>(_state,task)
        }
    }

    fun signUp(email : String, password : String, confirmPassword : String){
        if(!(_firebaseAuthRepository.isEmailValid(email))){
            _state.postValue(BaseState.Error(_context?.getString(R.string.invalid_email_text)))
            return
        }
        if(confirmPassword != password){
            _state.postValue(BaseState.Error(_context?.getString(R.string.passwords_do_not_match_text)))
            return
        }
        _firebaseAuthRepository.signUp(email, password).addOnCompleteListener { task->
            Utils.checkStatus<AuthResult>(_state,task)
        }
    }

    fun resetPassword(email: String){
        _firebaseAuthRepository.resetPassword(email).addOnCompleteListener { task->
            Utils.checkStatus<Void>(_state,task,_context?.getString(R.string.reset_password_mail_sent_text))
        }
    }


    fun isUserLoggedIn() : FirebaseUser?{
        return FirebaseAuthRepository.getAuthInstance().currentUser
    }

}