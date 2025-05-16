package com.example.mynotebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotebook.network.Todo
import com.example.mynotebook.network.User
import com.example.mynotebook.repository.TodoRepository
import com.example.mynotebook.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val todoRepository: TodoRepository = TodoRepository()
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _isLoadingUser = MutableStateFlow(false)
    val isLoadingUser: StateFlow<Boolean> = _isLoadingUser

    private val _isLoadingTodos = MutableStateFlow(false)
    val isLoadingTodos: StateFlow<Boolean> = _isLoadingTodos

    private val _errorUser = MutableStateFlow<String?>(null)
    val errorUser: StateFlow<String?> = _errorUser

    private val _errorTodos = MutableStateFlow<String?>(null)
    val errorTodos: StateFlow<String?> = _errorTodos

    fun fetchUser() {
        viewModelScope.launch {
            _isLoadingUser.value = true
            _errorUser.value = null
            try {
                _users.value = userRepository.getUsers()
            } catch (e: Exception) {
                _errorUser.value = "Nie udało się wczytać użytkownika"
            } finally {
                _isLoadingUser.value = false
            }
        }
    }

    fun fetchTodos(userId: Int) {
        viewModelScope.launch {
            _isLoadingTodos.value = true
            _errorTodos.value = null
            try {
                _todos.value = todoRepository.getTodosByUserId(userId)
            } catch (e: Exception) {
                _errorTodos.value = "Nie udało się wczytać zadań"
            } finally {
                _isLoadingTodos.value = false
            }
        }
    }
}