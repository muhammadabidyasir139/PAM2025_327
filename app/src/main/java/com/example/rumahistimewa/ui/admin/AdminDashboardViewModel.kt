package com.example.rumahistimewa.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val totalUsers: Int = 0,
    val totalVillas: Int = 0,
    val totalTransactions: Int = 0,
    val totalRevenue: Double = 0.0,
    val revenuePeriod: String = "week",
    val chartData: List<Float> = emptyList(),
    val chartRawData: List<Double> = emptyList(),
    val chartLabels: List<String> = emptyList(),
    val periodRevenue: Double = 0.0,
    val periodTransactions: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        fetchDashboardData()
        fetchRevenueData("week")
    }

    fun fetchRevenueData(period: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRevenue(period)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        val (labels, values, rawValues) = when (period) {
                            "week" -> {
                                val items = data.weeklyBreakdown ?: emptyList()
                                Triple(
                                    items.map { it.day.take(3) },
                                    items.map { it.revenue.toFloat() },
                                    items.map { it.revenue }
                                )
                            }
                            "month" -> {
                                val items = data.monthlyBreakdown ?: emptyList()
                                Triple(
                                    items.map { it.month }, // Changed from it.date to it.month based on user JSON
                                    items.map { it.revenue.toFloat() },
                                    items.map { it.revenue }
                                )
                            }
                            "day" -> {
                                val items = data.dailyBreakdown ?: emptyList()
                                Triple(
                                    items.map { it.hour },
                                    items.map { it.revenue.toFloat() },
                                    items.map { it.revenue }
                                )
                            }
                            else -> Triple(emptyList<String>(), emptyList<Float>(), emptyList<Double>())
                        }
                        
                        val maxVal = values.maxOrNull() ?: 1f
                        val normalized = if (maxVal == 0f) values else values.map { it / maxVal }
                        
                        _state.value = _state.value.copy(
                            revenuePeriod = period,
                            chartData = normalized,
                            chartRawData = rawValues,
                            chartLabels = labels,
                            periodRevenue = data.totalRevenue,
                            periodTransactions = data.totalTransactions
                        )
                    }
                }
            } catch (e: Exception) {
                // Keep old data or show error
                e.printStackTrace()
            }
        }
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Fetch Users
                val usersResponse = RetrofitClient.api.getUsers()
                val totalUsers = if (usersResponse.isSuccessful) {
                    usersResponse.body()?.count ?: 0
                } else {
                    0
                }

                // Fetch Villas
                val villasResponse = RetrofitClient.api.getAdminVillas()
                val totalVillas = if (villasResponse.isSuccessful) {
                    villasResponse.body()?.count ?: 0
                } else {
                    0
                }

                // Fetch Transactions & Revenue
                val transactionsResponse = RetrofitClient.api.getAdminTransactions()
                var totalTransactions = 0
                var totalRevenue = 0.0
                
                if (transactionsResponse.isSuccessful) {
                    val body = transactionsResponse.body()
                    if (body != null) {
                        totalTransactions = body.stats.totalTransactions
                        totalRevenue = body.stats.totalRevenue
                    }
                }

                _state.value = _state.value.copy(
                    totalUsers = totalUsers,
                    totalVillas = totalVillas,
                    totalTransactions = totalTransactions,
                    totalRevenue = totalRevenue,
                    isLoading = false
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
