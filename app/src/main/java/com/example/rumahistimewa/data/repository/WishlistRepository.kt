package com.example.rumahistimewa.data.repository

import kotlinx.coroutines.flow.Flow

class WishlistRepository(private val apiService: com.example.rumahistimewa.data.remote.ApiService) {

    suspend fun getWishlist(): Result<List<com.example.rumahistimewa.data.model.Villa>> {
        return try {
            val response = apiService.getWishlist()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch wishlist: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToWishlist(villaId: String): Result<String> {
        return try {
            val response = apiService.addToWishlist(com.example.rumahistimewa.data.model.WishlistRequest(villaId))
            if (response.isSuccessful) {
                Result.success("Added to wishlist")
            } else {
                Result.failure(Exception("Failed to add to wishlist: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromWishlist(villaId: String): Result<String> {
        return try {
            val response = apiService.removeFromWishlist(villaId)
            if (response.isSuccessful) {
                Result.success("Removed from wishlist")
            } else {
                Result.failure(Exception("Failed to remove from wishlist: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
