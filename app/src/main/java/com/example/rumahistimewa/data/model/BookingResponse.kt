package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    val message: String,
    val bookingId: Int,
    val totalAmount: Long,
    val payment: PaymentDetails
)

data class PaymentDetails(
    val orderId: String,
    val token: String,
    val redirectUrl: String
)

data class CreateBookingRequest(
    val villaId: Int,
    val checkIn: String,
    val checkOut: String
)
