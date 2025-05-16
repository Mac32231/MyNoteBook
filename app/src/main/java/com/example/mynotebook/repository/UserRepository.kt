package com.example.mynotebook.repository
import com.example.mynotebook.network.User
import com.example.mynotebook.network.RetrofitClient

class UserRepository {
        suspend fun getUsers(): List<User> {
            return RetrofitClient.apiService.getUsers()
        }
    }
