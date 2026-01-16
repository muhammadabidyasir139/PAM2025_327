package com.example.rumahistimewa.data.model

data class RevenueResponse(
    val message: String,
    val period: String,
    val data: RevenueData?
)

data class RevenueData(
    val totalTransactions: Int,
    val totalRevenue: Double,
    val dailyBreakdown: List<DailyBreakdown>? = null,
    val weeklyBreakdown: List<WeeklyBreakdown>? = null,
    val monthlyBreakdown: List<MonthlyBreakdown>? = null
)

data class DailyBreakdown(
    val hour: String,
    val revenue: Double
)

data class WeeklyBreakdown(
    val day: String,
    val revenue: Double
)

data class MonthlyBreakdown(
    val month: String, // Changed from date to month
    val revenue: Double
)
