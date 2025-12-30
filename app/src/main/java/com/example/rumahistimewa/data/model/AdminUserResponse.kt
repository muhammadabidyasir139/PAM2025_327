package com.example.rumahistimewa.data.model

data class AdminUserResponse(
    val command: String,
    val rowCount: Int,
    val rows: List<User>
)
