package com.example.rumahistimewa.ui.explore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class Villa(
    val id: String,
    val title: String,
    val location: String,
    val price: String,
    val rating: Double,
    val imageUrl: String? = null
)

class ExploreViewModel : ViewModel() {

    private val _villas = MutableStateFlow<List<Villa>>(emptyList())
    val villas: StateFlow<List<Villa>> = _villas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchVillas()
    }

    private fun sanitizeUrl(url: String?): String? {
        val cleaned = url?.trim()?.trim('`')?.trim()
        return cleaned?.takeIf { it.isNotEmpty() }
    }

    private fun fetchVillas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.getVillas()
                if (response.isSuccessful && response.body() != null) {
                    val apiVillas = response.body()!!
                    val uiVillas = apiVillas.map { apiVilla ->
                        val priceValue = apiVilla.price
                        val priceLong = priceValue.toLong()
                        val formattedPrice = java.text.NumberFormat.getIntegerInstance(
                            java.util.Locale("id", "ID")
                        ).format(priceLong)
                        Villa(
                            id = apiVilla.id,
                            title = apiVilla.name,
                            location = apiVilla.location,
                            price = "Rp $formattedPrice / night",
                            rating = 4.8,
                            imageUrl = sanitizeUrl(apiVilla.photos.firstOrNull())
                        )
                    }
                    _villas.value = uiVillas
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(2000) // 2 seconds delay as requested
            try {
                // Call fetchVillas internal logic without setting loading again immediately 
                // or just call fetchVillas which sets loading true (redundant but safe)
                // Better: extract fetch logic or just copy fetch logic here but with existing isLoading handling
                 val response = RetrofitClient.api.getVillas()
                if (response.isSuccessful && response.body() != null) {
                    val apiVillas = response.body()!!
                    val uiVillas = apiVillas.map { apiVilla ->
                        val priceValue = apiVilla.price
                        val priceLong = priceValue.toLong()
                        val formattedPrice = java.text.NumberFormat.getIntegerInstance(
                            java.util.Locale("id", "ID")
                        ).format(priceLong)
                        Villa(
                            id = apiVilla.id,
                            title = apiVilla.name,
                            location = apiVilla.location,
                            price = "Rp $formattedPrice / night",
                            rating = 4.8,
                            imageUrl = sanitizeUrl(apiVilla.photos.firstOrNull())
                        )
                    }
                    _villas.value = uiVillas
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        // Implement search logic if needed locally or via API
    }
}
