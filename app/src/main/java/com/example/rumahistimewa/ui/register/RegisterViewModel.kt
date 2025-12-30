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

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.register(
                    mapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password,
                        "role" to "customer"
                    )
                )

                if (response.isSuccessful) {
                    _registerState.value = "success"
                } else {
                    _registerState.value = "error"
                }
            } catch (e: Exception) {
                _registerState.value = e.message
            }
        }
    }
}
