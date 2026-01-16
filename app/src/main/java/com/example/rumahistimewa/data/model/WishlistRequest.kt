package com.example.rumahistimewa.data.model

data class WishlistRequest(
    val villaid: Int
)

data class WishlistVilla(
    val id: String,
    val name: String,
    val price: Double,
    val location: String,
    val description: String,
    val photos: List<String>
)
