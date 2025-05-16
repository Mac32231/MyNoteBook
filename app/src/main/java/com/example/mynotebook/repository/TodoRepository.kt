package com.example.mynotebook.repository

import com.example.mynotebook.network.Todo
import com.example.mynotebook.network.RetrofitClient


class TodoRepository {
    suspend fun getTodosByUserId(userId: Int): List<Todo> {
        return RetrofitClient.apiService.getTodosByUserId(userId)
    }
}