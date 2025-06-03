package com.example.mynotebook.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

object ProfileDataStore {

    private val NAME_KEY = stringPreferencesKey("name")
    private val SURNAME_KEY = stringPreferencesKey("surname")
    private val PHOTO_PATH_KEY = stringPreferencesKey("photo_path")

    fun getUserProfile(context: Context): Flow<UserProfile> {
        return context.dataStore.data.map { preferences ->
            UserProfile(
                name = preferences[NAME_KEY] ?: "",
                surname = preferences[SURNAME_KEY] ?: "",
                photoPath = preferences[PHOTO_PATH_KEY] ?: ""
            )
        }
    }

    suspend fun saveUserProfile(context: Context, profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = profile.name
            preferences[SURNAME_KEY] = profile.surname
            preferences[PHOTO_PATH_KEY] = profile.photoPath
        }
    }
}