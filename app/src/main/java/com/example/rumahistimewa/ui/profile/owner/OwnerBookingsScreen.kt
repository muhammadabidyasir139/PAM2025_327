package com.example.rumahistimewa.ui.profile.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.example.rumahistimewa.ui.theme.RedPrimary

data class MockBooking(
    val id: String,
    val guestName: String,
    val villaName: String,
    val date: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerBookingsScreen(
    onBackClick: () -> Unit
) {
    var bookings by remember { mutableStateOf<List<com.example.rumahistimewa.data.model.Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getOwnerBookings()
            if (response.isSuccessful) {
                bookings = response.body() ?: emptyList()
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
                title = { Text("Booking List", color = Color.White) },
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: com.example.rumahistimewa.data.model.Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(booking.villa?.name ?: "Unknown Villa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Guest: ${booking.user?.name ?: "Unknown Guest"}", style = MaterialTheme.typography.bodyMedium)
            Text("Date: ${booking.checkIn} - ${booking.checkOut}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = booking.status,
                style = MaterialTheme.typography.labelLarge,
                color = if (booking.status.equals("confirmed", ignoreCase = true)) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }
    }
}
