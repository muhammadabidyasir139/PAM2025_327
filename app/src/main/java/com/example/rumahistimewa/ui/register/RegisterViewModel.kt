package com.example.rumahistimewa.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState = _registerState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.register(
                    mapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password,
                        "role" to "customer"
                    )
                )

                _registerState.value = if (response.isSuccessful) "success" else "error"
            } catch (e: Exception) {
                _registerState.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
