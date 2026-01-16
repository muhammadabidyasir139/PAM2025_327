package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class AdminUserResponse(
    val message: String,
    val count: Int,
    val users: List<User>
)
