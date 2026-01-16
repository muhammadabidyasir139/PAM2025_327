package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

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

data class MyBookingItem(
    @SerializedName("payment_id") val paymentId: Int? = null,
    @SerializedName("bookingid") val bookingId: Int? = null,
    @SerializedName("booking_id") val bookingIdAlt: Int? = null,
    @SerializedName("userid") val userId: Int? = null,
    @SerializedName("villaid") val villaId: Int,
    @SerializedName("checkin") val checkIn: String,
    @SerializedName("checkout") val checkOut: String,
    @SerializedName("booking_status") val bookingStatus: String? = null,
    @SerializedName("booking_totalamount") val bookingTotalAmount: Long? = null,
    @SerializedName("villaname") val villaName: String? = null,
    @SerializedName("location") val location: String? = null,
    val payment: MyBookingPayment? = null
)

data class MyBookingPayment(
    val orderId: String? = null,
    val token: String? = null,
    val redirectUrl: String? = null
)

data class BookingDetailResponse(
    val message: String? = null,
    @SerializedName("booking") val booking: BookingDetail? = null,
    @SerializedName("data") val data: BookingDetail? = null
)

data class BookingDetail(
    val id: Int? = null,
    @SerializedName("bookingid") val bookingId: Int? = null,
    @SerializedName("booking_id") val bookingIdAlt: Int? = null,
    @SerializedName("userid") val userId: Int? = null,
    @SerializedName("villaid") val villaId: Int? = null,
    @SerializedName("checkin") val checkIn: String? = null,
    @SerializedName("check_in") val checkInAlt: String? = null,
    @SerializedName("checkout") val checkOut: String? = null,
    @SerializedName("check_out") val checkOutAlt: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("booking_status") val bookingStatus: String? = null,
    @SerializedName("totalamount") val totalAmount: Long? = null,
    @SerializedName("total_amount") val totalAmountAlt: Long? = null,
    @SerializedName("booking_totalamount") val bookingTotalAmount: Long? = null,
    @SerializedName("villaname") val villaName: String? = null,
    @SerializedName("location") val location: String? = null,
    val payment: MyBookingPayment? = null,
    val villa: Villa? = null,
    val user: User? = null
)
