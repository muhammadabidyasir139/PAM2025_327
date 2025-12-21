package com.example.rumahistimewa.data.model

data class Villa(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val location: String,
    val facilities: String,
    val photos: List<String>,
    val status: String, // pending, approved, rejected
    val ownerId: String,
    val owner: User? = null // Expanded owner details
)
