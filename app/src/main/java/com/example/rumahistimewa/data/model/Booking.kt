package com.example.rumahistimewa.data.model

data class Booking(
    val id: Int,
    @com.google.gson.annotations.SerializedName("userid") val userId: Int,
    @com.google.gson.annotations.SerializedName("villaid") val villaId: Int,
    @com.google.gson.annotations.SerializedName("checkin") val checkIn: String,
    @com.google.gson.annotations.SerializedName("checkout") val checkOut: String,
    @com.google.gson.annotations.SerializedName("status") val status: String,
    @com.google.gson.annotations.SerializedName("totalamount") val totalAmount: Long,
    @com.google.gson.annotations.SerializedName("villaname") val villaName: String? = null,
    @com.google.gson.annotations.SerializedName("location") val location: String? = null,
    val redirectUrl: String? = null,
    val villa: Villa? = null, 
    val user: User? = null
)
