package com.example.rumahistimewa.ui.profile.owner

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumahistimewa.data.model.Booking
import com.example.rumahistimewa.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    onBackClick: () -> Unit
) {
    var bookings by remember { mutableStateOf<List<com.example.rumahistimewa.data.model.Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Revenue State
    var totalRevenue by remember { mutableStateOf(0.0) }
    var monthlyBreakdown by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getOwnerBookings()
            if (response.isSuccessful) {
                bookings = response.body() ?: emptyList()
                
                // Calculate Revenue
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val monthFormat = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
                
                var total = 0.0
                val breakdown = mutableMapOf<String, Double>()

                bookings.forEach { booking ->
                    // Only count confirmed/completed
                     if (booking.status.equals("confirmed", ignoreCase = true) || booking.status.equals("completed", ignoreCase = true)) {
                         try {
                             val start = sdf.parse(booking.checkIn)?.time ?: 0L
                             val end = sdf.parse(booking.checkOut)?.time ?: 0L
                             val diff = end - start
                             val days = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
                             val price = booking.villa?.price ?: 0.0
                             val revenue = days * price
                             
                             total += revenue
                             
                             // Monthly
                             val dateObj = sdf.parse(booking.checkIn)
                             if (dateObj != null) {
                                 val monthKey = monthFormat.format(dateObj)
                                 breakdown[monthKey] = (breakdown[monthKey] ?: 0.0) + revenue
                             }
                         } catch (e: Exception) {
                             e.printStackTrace()
                         }
                     }
                }
                totalRevenue = total
                monthlyBreakdown = breakdown
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income / Revenue", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary)
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total Revenue", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("IDR ${java.text.NumberFormat.getIntegerInstance().format(totalRevenue.toLong())}", style = MaterialTheme.typography.displaySmall, color = RedPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Last updated: Just now", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }
                    }
                }
                
                item {
                    Text("Monthly Breakdown", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth()) // align start via modifier logic if needed, or fillMaxWidth text align
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(monthlyBreakdown.toList()) { (month, amount) ->
                    IncomeRow(month, "IDR ${java.text.NumberFormat.getIntegerInstance().format(amount.toLong())}")
                }
            }
        }
    }
}

@Composable
fun IncomeRow(month: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(month, style = MaterialTheme.typography.bodyLarge)
        Text(amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}
