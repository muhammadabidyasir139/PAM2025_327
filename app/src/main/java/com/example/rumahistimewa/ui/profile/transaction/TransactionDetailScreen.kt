package com.example.rumahistimewa.ui.profile.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rumahistimewa.data.model.TransactionDetail
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.ui.theme.RedPrimary
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    onBackClick: () -> Unit
) {
    var transaction by remember { mutableStateOf<TransactionDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(transactionId) {
        try {
            val response = RetrofitClient.api.getTransactionDetail(transactionId)
            if (response.isSuccessful) {
                transaction = response.body()?.transaction
            } else {
                error = "Failed to load transaction details"
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Detail", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = RedPrimary
                )
            } else if (error != null) {
                Text(
                    text = error ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (transaction != null) {
                val item = transaction!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Villa Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            AsyncImage(
                                model = if (item.villaPhoto.startsWith("http")) item.villaPhoto else "${RetrofitClient.BASE_URL}${item.villaPhoto}",
                                contentDescription = item.villaName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(item.villaName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(item.villaLocation, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    }

                    // Transaction Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRow("Order ID", item.orderId)
                            DetailRow("Transaction ID", item.transactionId)
                            DetailRow("Status", item.transactionStatus)
                            DetailRow("Payment Type", item.paymentType)
                            DetailRow("Time", item.transactionTime)
                            HorizontalDivider()
                            DetailRow("Check-in", item.checkIn.split("T")[0])
                            DetailRow("Check-out", item.checkOut.split("T")[0])
                            HorizontalDivider()
                            
                            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            DetailRow("Total Amount", format.format(item.grossAmount))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
