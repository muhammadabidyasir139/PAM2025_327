package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class TransactionDetailResponse(
    val message: String,
    val transaction: TransactionDetail
)

data class TransactionDetail(
    @SerializedName("orderid") val orderId: String,
    @SerializedName("transactionid") val transactionId: String,
    @SerializedName("paymenttype") val paymentType: String,
    @SerializedName("transactionstatus") val transactionStatus: String,
    @SerializedName("transactiontime") val transactionTime: String,
    @SerializedName("grossamount") val grossAmount: Double,
    @SerializedName("checkin") val checkIn: String,
    @SerializedName("checkout") val checkOut: String,
    @SerializedName("bookingid") val bookingId: Int,
    @SerializedName("villaname") val villaName: String,
    @SerializedName("villalocation") val villaLocation: String,
    @SerializedName("villaphoto") val villaPhoto: String
)
