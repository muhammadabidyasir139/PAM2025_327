package com.example.rumahistimewa.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    villaId: String? = null,
    onBookingSuccess: () -> Unit = {}
) {
    val dateRangePickerState = rememberDateRangePickerState()
    val scope = rememberCoroutineScope()
    var isAvailable by remember { mutableStateOf<Boolean?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    var bookingStatus by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    fun formatMillis(millis: Long?): String {
        return if (millis != null) {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            formatter.format(Date(millis))
        } else {
            "Select Date"
        }
    }
    
    fun formatApiDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun sanitizeUrl(url: String?): String? {
        val cleaned = url?.trim()?.trim('`')?.trim()
        return cleaned?.takeIf { it.isNotEmpty() }
    }

    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis
        if (villaId != null && start != null && end != null) {
            isChecking = true
            isAvailable = null
            try {
               val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.checkAvailability(
                   id = villaId,
                   checkIn = formatApiDate(start),
                   checkOut = formatApiDate(end)
               )
               if (response.isSuccessful) {
                   isAvailable = response.body()?.get("available") as? Boolean ?: true
               }
            } catch (e: Exception) {
               e.printStackTrace()
            } finally {
               isChecking = false
            }
        } else {
            isAvailable = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Dates", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (bookingStatus != null && !bookingStatus!!.contains("Success")) {
                    Text(
                        text = bookingStatus!!,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                        color = Color.Red
                    )
                }
                
                var villaPrice by remember { mutableStateOf(0.0) }
                LaunchedEffect(villaId) {
                    if (villaId != null) {
                        try {
                             val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getVillaDetail(villaId)
                             if (response.isSuccessful) {
                                 villaPrice = response.body()?.price ?: 0.0
                             }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                val start = dateRangePickerState.selectedStartDateMillis
                val end = dateRangePickerState.selectedEndDateMillis
                var totalPrice = 0.0
                
                if (start != null && end != null && villaPrice > 0) {
                     val days = (end - start) / (1000 * 60 * 60 * 24)
                     if (days > 0) {
                         totalPrice = days * villaPrice
                     }
                }
                
                if (totalPrice > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                         Row(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(16.dp),
                             horizontalArrangement = Arrangement.SpaceBetween,
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             Text(
                                 text = "Total Price:",
                                 style = MaterialTheme.typography.titleMedium,
                                 color = Color.Black
                             )
                             Text(
                                 text = "Rp ${java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID")).format(totalPrice)}",
                                 style = MaterialTheme.typography.titleLarge,
                                 fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                 color = RedPrimary
                             )
                         }
                    }
                }


                var showPaymentDialog by remember { mutableStateOf(false) }
                var paymentUrl by remember { mutableStateOf<String?>(null) }
                
                if (showPaymentDialog && paymentUrl != null) {
                    AlertDialog(
                        onDismissRequest = { 
                            showPaymentDialog = false
                            onBookingSuccess() 
                        },
                        title = { Text("Booking Created") },
                        text = { Text("Please complete your payment to confirm the booking.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(paymentUrl))
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Pay Now")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showPaymentDialog = false
                                onBookingSuccess() 
                            }) {
                                Text("Later")
                            }
                        }
                    )
                }

                Button(
                    onClick = { 
                        scope.launch {
                            val start = dateRangePickerState.selectedStartDateMillis
                            val end = dateRangePickerState.selectedEndDateMillis
                            if (villaId != null && start != null && end != null) {
                                val parsedVillaId = villaId.toIntOrNull()
                                if (parsedVillaId == null) {
                                    bookingStatus = "Booking Failed: villaId tidak valid"
                                    return@launch
                                }
                                try {
                                    val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.createBooking(
                                        com.example.rumahistimewa.data.model.CreateBookingRequest(
                                            villaId = parsedVillaId,
                                            checkIn = formatApiDate(start),
                                            checkOut = formatApiDate(end)
                                        )
                                    )
                                    if (response.isSuccessful && response.body() != null) {
                                        val bookingResp = response.body()!!
                                        bookingStatus = "Booking Successful!"
                                        paymentUrl = sanitizeUrl(bookingResp.payment.redirectUrl)
                                        showPaymentDialog = true
                                    } else {
                                        bookingStatus = "Booking Failed: ${response.message()}"
                                    }
                                } catch (e: Exception) {
                                    bookingStatus = "Error: ${e.message}"
                                }
                            }
                        }
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null 
                              && dateRangePickerState.selectedEndDateMillis != null
                              && (isAvailable != false),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedSecondary,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else if (isAvailable == false) {
                        Text("Not Available")
                    } else {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val start = dateRangePickerState.selectedStartDateMillis
            val end = dateRangePickerState.selectedEndDateMillis
            val dateText = if (start != null && end != null) {
                "${formatMillis(start)} - ${formatMillis(end)}"
            } else if (start != null) {
                "${formatMillis(start)} - Checkout?"
            } else {
                "Select Check-in Date"
            }

            Text(
                text = dateText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
