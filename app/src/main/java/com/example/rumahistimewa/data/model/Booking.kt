package com.example.rumahistimewa.data.model

data class Booking(
    val id: String,
    val villaId: String,
    val userId: String,
    val checkIn: String,
    val checkOut: String,
    val status: String, // confirmed, pending, etc.
    val villa: Villa? = null, // Optional expanded villa details
    val user: User? = null // Optional expanded user details
)
