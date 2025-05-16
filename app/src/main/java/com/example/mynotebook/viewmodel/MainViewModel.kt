package com.example.mynotebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotebook.network.Post
import com.example.mynotebook.network.User
import com.example.mynotebook.repository.PostRepository
import com.example.mynotebook.repository.UserRepository
import com.example.mynotebook.network.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val postRepository: PostRepository = PostRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _postState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val postState: StateFlow<UiState<List<Post>>> = _postState

    private val _userState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val userState: StateFlow<UiState<List<User>>> = _userState

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _postState.value = UiState.Loading
            _userState.value = UiState.Loading

            try {
                val posts = postRepository.getPosts()
                val users = userRepository.getUsers()

                _postState.value = UiState.Success(posts)
                _userState.value = UiState.Success(users)
            } catch (e: Exception) {
                val errorMessage = "Błąd pobierania danych"
                _postState.value = UiState.Error(errorMessage)
                _userState.value = UiState.Error(errorMessage)
            }
        }
    }
}