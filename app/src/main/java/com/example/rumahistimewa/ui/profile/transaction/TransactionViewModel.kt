package com.example.rumahistimewa.ui.profile.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.model.Transaction
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.getUserTransactions()
                if (response.isSuccessful) {
                    _transactions.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = "Failed to fetch transactions: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createBooking(villaId: String, date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _paymentUrl.value = null
            try {
                val payload = mapOf(
                    "villa_id" to villaId,
                    "check_in" to date,
                    "check_out" to date // For now handled as 1 day booking or logic needs update if multiday
                )
                val response = RetrofitClient.api.createBooking(payload)
                if (response.isSuccessful) {
                    val body = response.body()
                    _paymentUrl.value = body?.payment?.redirectUrl
                } else {
                     _error.value = "Failed to create booking: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
