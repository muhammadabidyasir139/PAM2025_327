package com.example.rumahistimewa.data.remote

import com.example.rumahistimewa.data.model.AuthResponse
import com.example.rumahistimewa.data.model.BookingDetailResponse
import com.example.rumahistimewa.data.model.CreateBookingRequest
import com.example.rumahistimewa.data.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.rumahistimewa.data.model.User


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
        @Body body: CreateBookingRequest
    ): Response<com.example.rumahistimewa.data.model.BookingResponse>

    @retrofit2.http.GET("bookings/my")
    suspend fun getMyBookings(): Response<List<com.example.rumahistimewa.data.model.MyBookingItem>>

    @retrofit2.http.GET("bookings/{id}")
    suspend fun getBookingDetail(
        @retrofit2.http.Path("id") id: Int
    ): Response<BookingDetailResponse>

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

    @retrofit2.http.Multipart
    @retrofit2.http.PUT("owner/villas/{id}")
    suspend fun updateVilla(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Part("name") name: okhttp3.RequestBody,
        @retrofit2.http.Part("location") location: okhttp3.RequestBody,
        @retrofit2.http.Part("price") price: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part photos: List<okhttp3.MultipartBody.Part>? = null
    ): Response<Map<String, Any>>

    @retrofit2.http.DELETE("owner/villas/{id}")
    suspend fun deleteVilla(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.GET("owner/bookings")
    suspend fun getOwnerBookings(): Response<List<com.example.rumahistimewa.data.model.Booking>>

    @retrofit2.http.GET("user/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @retrofit2.http.Multipart
    @retrofit2.http.PUT("user/profile")
    suspend fun updateProfile(
        @retrofit2.http.Part("name") name: okhttp3.RequestBody,
        @retrofit2.http.Part("email") email: okhttp3.RequestBody,
        @retrofit2.http.Part("phone") phone: okhttp3.RequestBody,
        @retrofit2.http.Part photo: okhttp3.MultipartBody.Part? = null
    ): Response<UserProfileResponse>

    @retrofit2.http.POST("profile/change-password")
    suspend fun changePassword(
        @Body body: com.example.rumahistimewa.data.model.ChangePasswordRequest
    ): Response<Map<String, String>>

    // Admin methods moved to bottom


    @retrofit2.http.GET("admin/villas")
    suspend fun getAdminVillas(): Response<com.example.rumahistimewa.data.model.AdminVillaResponse>

    @retrofit2.http.PUT("admin/villas/{id}/approve")
    suspend fun approveVilla(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.PUT("admin/villas/{id}/reject")
    suspend fun rejectVilla(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.DELETE("admin/villas/{id}")
    suspend fun deleteVillaAdmin(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    @retrofit2.http.Multipart
    @retrofit2.http.PUT("admin/villas/{id}")
    suspend fun updateVillaAdmin(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Part("name") name: okhttp3.RequestBody,
        @retrofit2.http.Part("location") location: okhttp3.RequestBody,
        @retrofit2.http.Part("price") price: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part photos: List<okhttp3.MultipartBody.Part>? = null
    ): Response<Map<String, Any>>

    @retrofit2.http.PUT("admin/villas/{id}")
    suspend fun updateVillaAdminJson(
        @retrofit2.http.Path("id") id: String,
        @retrofit2.http.Body body: com.example.rumahistimewa.data.model.UpdateVillaRequest
    ): Response<Map<String, Any>>

    // Wishlist
    @retrofit2.http.GET("wishlist/my")
    suspend fun getWishlist(): Response<List<com.example.rumahistimewa.data.model.WishlistVilla>>

    @POST("wishlist")
    suspend fun addToWishlist(
        @Body body: com.example.rumahistimewa.data.model.WishlistRequest
    ): Response<Map<String, Any>>

    @retrofit2.http.DELETE("wishlist/{id}")
    suspend fun removeFromWishlist(
        @retrofit2.http.Path("id") id: String
    ): Response<Map<String, Any>>

    // Transactions
    @retrofit2.http.GET("user/transactions")
    suspend fun getUserTransactions(): Response<com.example.rumahistimewa.data.model.TransactionResponse>

    @retrofit2.http.GET("user/transactions/{id}")
    suspend fun getTransactionDetail(
        @retrofit2.http.Path("id") id: String
    ): Response<com.example.rumahistimewa.data.model.TransactionDetailResponse>

    @retrofit2.http.GET("owner/income")
    suspend fun getOwnerIncome(): Response<com.example.rumahistimewa.data.model.IncomeResponse>

    @retrofit2.http.GET("admin/transactions")

    suspend fun getAdminTransactions(): Response<com.example.rumahistimewa.data.model.AdminTransactionResponse>

    @retrofit2.http.GET("admin/transactions/{id}")
    suspend fun getAdminTransactionDetail(
        @retrofit2.http.Path("id") id: String
    ): Response<com.example.rumahistimewa.data.model.AdminTransactionItem>

    @retrofit2.http.GET("admin/users")
    suspend fun getUsers(): Response<com.example.rumahistimewa.data.model.AdminUserResponse>

    @retrofit2.http.GET("admin/users/{id}")
    suspend fun getUser(@retrofit2.http.Path("id") id: Int): Response<User>

    @retrofit2.http.PATCH("admin/users/{id}")
    suspend fun updateUserStatus(
        @retrofit2.http.Path("id") id: Int,
        @retrofit2.http.Body body: Map<String, String>
    ): Response<Unit>

    @retrofit2.http.GET("admin/revenue")
    suspend fun getRevenue(
        @retrofit2.http.Query("period") period: String
    ): Response<com.example.rumahistimewa.data.model.RevenueResponse>
}
