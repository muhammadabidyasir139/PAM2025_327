package com.example.rumahistimewa.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.repository.WishlistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(private val repository: WishlistRepository) : ViewModel() {

    private val _wishlistItems = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.rumahistimewa.data.model.Villa>>(emptyList())
    val wishlistItems: StateFlow<List<com.example.rumahistimewa.data.model.Villa>> = _wishlistItems.asStateFlow()
    
    private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchWishlist() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getWishlist()
                .onSuccess { 
                    _wishlistItems.value = it 
                }
                .onFailure {
                    // Handle error (maybe expose error state)
                }
            _isLoading.value = false
        }
    }

    fun addToWishlist(villaId: String) {
        viewModelScope.launch {
            repository.addToWishlist(villaId)
                .onSuccess {
                    fetchWishlist() // Refresh list
                }
        }
    }

    fun removeFromWishlist(id: String) {
        viewModelScope.launch {
            repository.removeFromWishlist(id)
                .onSuccess {
                    fetchWishlist() // Refresh list
                }
        }
    }
    
    fun isWishlisted(id: String): StateFlow<Boolean> {
         return wishlistItems.map { list ->
             list.any { it.id == id }
         }.stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = false
         )
    }
}
