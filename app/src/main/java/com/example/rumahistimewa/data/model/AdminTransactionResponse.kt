package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class AdminTransactionResponse(
    val message: String,
    val pagination: Pagination,
    val transactions: List<AdminTransactionItem>,
    val stats: TransactionStats
)

data class TransactionStats(
    val totalTransactions: Int,
    val totalRevenue: Double
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class AdminTransactionItem(
    @SerializedName("orderid") val orderId: String,
    @SerializedName("transactionid") val transactionId: String?,
    @SerializedName("paymenttype") val paymentType: String?,
    @SerializedName("transactionstatus") val transactionStatus: String,
    @SerializedName("transactiontime") val transactionTime: String?,
    @SerializedName("grossamount") val grossAmount: Double,
    @SerializedName("checkin") val checkIn: String,
    @SerializedName("checkout") val checkOut: String,
    @SerializedName("villaname") val villaName: String,
    @SerializedName("villalocation") val villaLocation: String,
    @SerializedName("customername") val customerName: String,
    @SerializedName("customeremail") val customerEmail: String
)
