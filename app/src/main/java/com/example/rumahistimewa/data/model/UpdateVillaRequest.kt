package com.example.rumahistimewa.data.model

data class UpdateVillaRequest(
    val name: String,
    val location: String,
    val price: Double,
    val description: String,
    val photos: List<String>? = null
)
