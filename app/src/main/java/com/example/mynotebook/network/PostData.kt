package com.example.mynotebook.network

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)

data class Company(
    val name: String
)
data class Geo(
    val lat: String,
    val lng: String
)
data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val company: Company?,
    val address: Address?
)