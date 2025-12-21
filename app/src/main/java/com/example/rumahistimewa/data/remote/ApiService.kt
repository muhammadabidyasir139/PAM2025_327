package com.example.rumahistimewa.data.remote

import com.example.rumahistimewa.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: Map<String, String>
    ): Response<Map<String, String>>

    // Customer
    @retrofit2.http.GET("villas")
    suspend fun getVillas(): Response<List<com.example.rumahistimewa.data.model.Villa>>

    @retrofit2.http.GET("villas/{id}")
    suspend fun getVillaDetail(
        @retrofit2.http.Path("id") id: String
    ): Response<com.example.rumahistimewa.data.model.Villa>

    @retrofit2.http.GET("availability/{id}")
    suspend fun checkAvailability(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Query("checkIn") checkIn: String,
        @retrofit2.http.Query("checkOut") checkOut: String
    ): Response<Map<String, Any>>

    @POST("bookings")
    suspend fun createBooking(
        @Body body: Map<String, Any>
    ): Response<Map<String, Any>>

    @retrofit2.http.GET("bookings/my")
    suspend fun getMyBookings(): Response<List<com.example.rumahistimewa.data.model.Booking>>

    // Owner
    @retrofit2.http.GET("owner/villas")
    suspend fun getOwnerVillas(): Response<List<com.example.rumahistimewa.data.model.Villa>>

    @retrofit2.http.Multipart
    @POST("owner/villas")
    suspend fun createVilla(
        @retrofit2.http.Part("name") name: okhttp3.RequestBody,
        @retrofit2.http.Part("location") location: okhttp3.RequestBody,
        @retrofit2.http.Part("price") price: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part photos: List<okhttp3.MultipartBody.Part>
    ): Response<Map<String, Any>>

    @retrofit2.http.PUT("owner/villas/{id}")
    suspend fun updateVilla(
        @retrofit2.http.Path("id") id: String,
        @Body body: Map<String, Any>
    ): Response<Map<String, Any>>

    @retrofit2.http.DELETE("owner/villas/{id}")
    suspend fun deleteVilla(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.GET("owner/bookings")
    suspend fun getOwnerBookings(): Response<List<com.example.rumahistimewa.data.model.Booking>>

    // Admin
    @retrofit2.http.GET("admin/users")
    suspend fun getUsers(): Response<List<com.example.rumahistimewa.data.model.User>>

    @retrofit2.http.PUT("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @retrofit2.http.Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>

    @retrofit2.http.GET("admin/villas")
    suspend fun getAdminVillas(): Response<List<com.example.rumahistimewa.data.model.Villa>>

    @retrofit2.http.PUT("admin/villas/{id}/approve")
    suspend fun approveVilla(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.DELETE("admin/villas/{id}")
    suspend fun deleteVillaAdmin(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>
}
