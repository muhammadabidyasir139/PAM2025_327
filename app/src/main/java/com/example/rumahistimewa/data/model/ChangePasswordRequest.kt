package com.example.rumahistimewa.data.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
