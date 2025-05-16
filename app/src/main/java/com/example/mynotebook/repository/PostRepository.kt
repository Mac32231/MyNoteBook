package com.example.mynotebook.repository
import com.example.mynotebook.network.Post
import com.example.mynotebook.network.RetrofitClient


class PostRepository {
    suspend fun getPosts(): List<Post> {
        return RetrofitClient.apiService.getPosts()
    }
}
