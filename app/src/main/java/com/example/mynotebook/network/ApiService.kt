package com.example.mynotebook.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("todos")
    suspend fun getTodosByUserId(@Query("userId") userId: Int): List<Todo>
}