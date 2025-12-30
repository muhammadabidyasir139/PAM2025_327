package com.example.rumahistimewa.data.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    val message: String,
    @SerializedName("transactions") val data: List<Transaction>
)

data class Transaction(
    @SerializedName("orderid") val orderId: String,
    @SerializedName("transactionid") val transactionId: String?,
    @SerializedName("paymenttype") val paymentType: String?,
    @SerializedName("transactionstatus") val status: String,
    @SerializedName("transactiontime") val transactionTime: String?,
    @SerializedName("grossamount") val amount: Double,
    @SerializedName("villaname") val villaName: String
)
