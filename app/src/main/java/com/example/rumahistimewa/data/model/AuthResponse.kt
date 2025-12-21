package com.example.rumahistimewa.data.model

data class AuthResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val role: String,
    val email: String? = null,
    val isActive: Boolean = true // Default true if missing
)