package com.example.rumahistimewa.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.model.ChangePasswordRequest
import com.example.rumahistimewa.data.model.ProfileResponse
import com.example.rumahistimewa.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileResponse?>(null)
    val profileState = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _updateState = MutableStateFlow<Boolean?>(null)
    val updateState = _updateState.asStateFlow()
    
    private val _passwordChangeState = MutableStateFlow<String?>(null)
    val passwordChangeState = _passwordChangeState.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProfile().collect { result ->
                result.onSuccess {
                    _profileState.value = it
                    _error.value = null
                }.onFailure {
                    _error.value = it.message
                }
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, photoFile: File?) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateState.value = null
            repository.updateProfile(name, email, phone, photoFile).collect { result ->
                result.onSuccess {
                    _profileState.value = it
                    _updateState.value = true
                    _error.value = null
                }.onFailure {
                    _updateState.value = false
                    _error.value = it.message
                }
                _isLoading.value = false
            }
        }
    }
    
    fun resetUpdateState() {
        _updateState.value = null
    }

    fun changePassword(request: ChangePasswordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _passwordChangeState.value = null
            repository.changePassword(request).collect { result ->
                result.onSuccess {
                    _passwordChangeState.value = "Success"
                    _error.value = null
                }.onFailure {
                    _passwordChangeState.value = it.message
                }
                _isLoading.value = false
            }
        }
    }
    
    fun resetPasswordChangeState() {
        _passwordChangeState.value = null
    }
}
