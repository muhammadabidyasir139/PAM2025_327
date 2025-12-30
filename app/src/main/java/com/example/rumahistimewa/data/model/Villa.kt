package com.example.rumahistimewa.data.model

data class Villa(
    @com.google.gson.annotations.SerializedName("id")
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val location: String,
    val facilities: String? = null,
    val photos: List<String>,
    val status: String, // pending, approved, rejected
    @com.google.gson.annotations.SerializedName("ownerid")
    val ownerId: String,
    val owner: User? = null // Expanded owner details
)
