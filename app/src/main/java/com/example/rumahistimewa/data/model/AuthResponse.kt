package com.example.rumahistimewa.data.model

data class AuthResponse(
    val message: String,
    val token: String,
    val user: User
)

