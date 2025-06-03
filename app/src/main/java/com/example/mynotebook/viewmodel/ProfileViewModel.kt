package com.example.mynotebook.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotebook.data.ProfileDataStore
import com.example.mynotebook.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _surname = MutableStateFlow("")
    val surname: StateFlow<String> = _surname

    private val _photoPath = MutableStateFlow("")
    val photoPath: StateFlow<String> = _photoPath

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profile = ProfileDataStore.getUserProfile(context).first()
            _name.value = profile.name
            _surname.value = profile.surname
            _photoPath.value = profile.photoPath
        }
    }

    fun saveData(name: String, surname: String, photoPath: String) {
        viewModelScope.launch {
            ProfileDataStore.saveUserProfile(context, UserProfile(name, surname, photoPath))
            _name.value = name
            _surname.value = surname
            _photoPath.value = photoPath
        }
    }
}