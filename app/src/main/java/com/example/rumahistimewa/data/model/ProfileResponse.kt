package com.example.rumahistimewa.data.model

data class ProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val photo: String? = null,
    val phone: String? = null
)

data class UserProfileResponse(
    val message: String,
    val user: ProfileResponse
)
