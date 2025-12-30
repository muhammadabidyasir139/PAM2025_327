package com.example.rumahistimewa.data.model

data class ProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val photo: String? = null,
    val createdAt: String,
    val updatedAt: String
)
