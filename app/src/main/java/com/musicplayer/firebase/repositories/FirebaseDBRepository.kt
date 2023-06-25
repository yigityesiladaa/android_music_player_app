package com.musicplayer.firebase.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.musicplayer.firebase.services.IFirebaseDBService
import com.musicplayer.models.Music
import com.musicplayer.models.MusicCategory

class FirebaseDBRepository : IFirebaseDBService<MusicCategory> {

    private val currentUserId = FirebaseAuthRepository.getCurrentUserId()

    companion object {
        const val CATEGORIES_COLLECTION_NAME = "musicCategories"
        const val FAVORITES_COLLECTION_NAME = "favorites"

        fun getFirebaseDBReference(collection: String): DatabaseReference {
            return FirebaseDatabase.getInstance().getReference(collection)
        }
    }

    override fun getAll(): Task<DataSnapshot>? {
        val dbRef = getFirebaseDBReference(CATEGORIES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).get()
        }
        return null
    }

    override fun insertToFavorites(music: Music): Task<Void>? {
        val dbRef = getFirebaseDBReference(FAVORITES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).child(music.mid).setValue(music)
        }
        return null
    }

    override fun getAllFavorites(): Task<DataSnapshot>? {
        val dbRef = getFirebaseDBReference(FAVORITES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).get()
        }
        return null
    }

    override fun getFavoriteById(id: String): Task<DataSnapshot>? {
        val dbRef = getFirebaseDBReference(FAVORITES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).child(id).get()
        }
        return null
    }

    override fun removeFromFavorites(id: String): Task<Void>? {
        val dbRef = getFirebaseDBReference(FAVORITES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).child(id).removeValue()
        }

        return null
    }

    override fun insert(data: MusicCategory): Task<Void>? {
        val dbRef = getFirebaseDBReference(CATEGORIES_COLLECTION_NAME)
        currentUserId?.let {
            return dbRef.child(it).child(data.catId).setValue(data)
        }
        return null
    }

    fun getFID(): String? {
        val dbRef = getFirebaseDBReference(CATEGORIES_COLLECTION_NAME)
        return dbRef.push().key
    }

}