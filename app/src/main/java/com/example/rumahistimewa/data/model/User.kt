package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("status") val status: String? = "active"
)