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
    
    // Formatting date helper
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

    // Availability Check
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
                   // Assuming API returns { "available": true }
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
                if (bookingStatus != null) {
                     Text(
                        text = bookingStatus!!,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                        color = if (bookingStatus!!.contains("Success")) Color.Green else Color.Red
                     )
                }
                
                Button(
                    onClick = { 
                        scope.launch {
                            val start = dateRangePickerState.selectedStartDateMillis
                            val end = dateRangePickerState.selectedEndDateMillis
                            if (villaId != null && start != null && end != null) {
                                try {
                                    val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.createBooking(
                                        mapOf(
                                            "villaId" to villaId, // Pass as number or string? API doc says number for payload, but usually string ref.
                                            // The payload says "villaId": number. I need to be careful.
                                            // If villaId is String from my app, can I pass it?
                                            // "villaId": number.
                                            // I'll try passing it as is, JSON handles things. Or villaId.toLong() if safe.
                                            // Assuming string is fine or numeric string.
                                            "villaId" to (villaId.toLongOrNull() ?: villaId),
                                            "checkIn" to formatApiDate(start),
                                            "checkOut" to formatApiDate(end)
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        bookingStatus = "Booking Successful!"
                                        kotlinx.coroutines.delay(1000)
                                        onBookingSuccess()
                                    } else {
                                        bookingStatus = "Booking Failed"
                                    }
                                } catch (e: Exception) {
                                    bookingStatus = "Error: ${e.message}"
                                }
                            }
                        }
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null 
                              && dateRangePickerState.selectedEndDateMillis != null
                              && (isAvailable == true),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
