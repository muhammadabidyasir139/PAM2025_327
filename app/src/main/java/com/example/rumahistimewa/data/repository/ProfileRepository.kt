package com.example.rumahistimewa.data.repository

import com.example.rumahistimewa.data.model.ChangePasswordRequest
import com.example.rumahistimewa.data.model.ProfileResponse
import com.example.rumahistimewa.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import retrofit2.Response

class ProfileRepository(private val apiService: ApiService) {

    fun getProfile(): Flow<Result<ProfileResponse>> = flow {
        try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch profile: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        photoFile: File?
    ): Flow<Result<ProfileResponse>> = flow {
        try {
            val textMediaType = "text/plain".toMediaTypeOrNull()
            val namePart = name.toRequestBody(textMediaType)
            val emailPart = email.toRequestBody(textMediaType)
            val phonePart = phone.toRequestBody(textMediaType)

            val photoPart = photoFile?.let {
                val imageMediaType = "image/*".toMediaTypeOrNull()
                val requestFile = it.asRequestBody(imageMediaType)
                MultipartBody.Part.createFormData("photo", it.name, requestFile)
            }

            val response = apiService.updateProfile(namePart, emailPart, phonePart, photoPart)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to update profile: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun changePassword(request: ChangePasswordRequest): Flow<Result<String>> = flow {
        try {
            val response = apiService.changePassword(request)
            if (response.isSuccessful) {
                emit(Result.success("Password changed successfully"))
            } else {
                emit(Result.failure(Exception("Failed to change password: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
