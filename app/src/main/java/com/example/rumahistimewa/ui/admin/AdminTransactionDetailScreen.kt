package com.example.rumahistimewa.ui.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.data.model.AdminTransactionItem
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.ui.theme.RedPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTransactionDetailScreen(
    orderId: String,
    onBackClick: () -> Unit
) {
    var transaction by remember { mutableStateOf<AdminTransactionItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(orderId) {
        try {
            val response = RetrofitClient.api.getAdminTransactionDetail(orderId)
            if (response.isSuccessful) {
                transaction = response.body()
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
                title = { Text("Transaction Detail", color = Color.White) },
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
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else if (transaction != null) {
                val tx = transaction!!
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    DetailSection(title = "Transaction Info") {
                        DetailItem("Order ID", tx.orderId)
                        DetailItem("Status", tx.transactionStatus, isStatus = true)
                        DetailItem("Amount", "Rp ${String.format("%,.0f", tx.grossAmount)}", isPrice = true)
                        DetailItem("Payment Type", tx.paymentType ?: "-")
                        DetailItem("Transaction Time", tx.transactionTime ?: "-")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailSection(title = "Villa Details") {
                        DetailItem("Villa Name", tx.villaName)
                        DetailItem("Location", tx.villaLocation)
                        DetailItem("Check In", formatDate(tx.checkIn))
                        DetailItem("Check Out", formatDate(tx.checkOut))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailSection(title = "Customer Info") {
                        DetailItem("Name", tx.customerName)
                        DetailItem("Email", tx.customerEmail)
                    }
                }
            } else {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Transaction not found", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RedPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, isStatus: Boolean = false, isPrice: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.SemiBold,
            color = if (isStatus) {
                 if (value == "success" || value == "settlement") Color.Green else if (value == "pending") Color(0xFFFFA000) else Color.Red
            } else if (isPrice) {
                RedPrimary
            } else {
                Color.Black
            }
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: "")
    } catch (e: Exception) {
        dateString
    }
}
