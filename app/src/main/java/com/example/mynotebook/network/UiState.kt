package com.example.mynotebook.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String?) : UiState<Nothing>()

    val isLoading get() = this is Loading
    val isError get() = this is Error
    val isSuccess get() = this is Success<*>
}

// -- Skróty do tworzenia stanu

fun <T> loadingState(): UiState<T> = UiState.Loading
fun <T> successState(data: T): UiState<T> = UiState.Success(data)
fun <T> errorState(message: String?): UiState<T> = UiState.Error(message)