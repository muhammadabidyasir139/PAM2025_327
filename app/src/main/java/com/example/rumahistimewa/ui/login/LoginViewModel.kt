package com.example.rumahistimewa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Mock for testing specific roles if API fails or for quick access (OPTIONAL, can remove if strict)
                // if (email == "admin") ... 
                // Strict API implementation as requested:
                
                val response = RetrofitClient.api.login(
                    mapOf(
                        "email" to email,
                        "password" to password
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val token = authResponse.token
                    val user = authResponse.user
                    
                    _loginState.value = user.role
                    com.example.rumahistimewa.util.UserSession.login(user.role, user.id, token)
                } else {
                    _loginState.value = "error"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginState.value = "error"
            }
        }
    }
}
