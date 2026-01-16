package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun TransactionListScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var transactions by remember { mutableStateOf<List<AdminTransactionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAdminTransactions()
            if (response.isSuccessful) {
                transactions = response.body()?.transactions ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    AdminLayout(
        title = "Transactions",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Villa", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.titleSmall)
                        Text("Customer", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.titleSmall)
                        Text("Date", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Text("Status", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()

                    LazyColumn {
                        items(transactions) { tx ->
                            TransactionRow(tx = tx, onClick = { 
                                // Navigate to detail with orderId
                                onNavigate("admin_transaction_detail/${tx.orderId}")
                            })
                            HorizontalDivider()
                        }
                    }
                    
                    if (transactions.isEmpty()) {
                         Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No transactions found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRow(tx: AdminTransactionItem, onClick: () -> Unit) {
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val outputFormat = SimpleDateFormat("dd MMM", Locale.US)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            dateString.take(10)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(tx.villaName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Rp ${String.format("%,.0f", tx.grossAmount)}", style = MaterialTheme.typography.bodySmall, color = RedPrimary)
        }
        Text(tx.customerName, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
        Text(formatDate(tx.checkIn), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        
        Text(
            tx.transactionStatus, 
            modifier = Modifier.weight(0.8f), 
            style = MaterialTheme.typography.bodySmall,
            color = if (tx.transactionStatus == "success" || tx.transactionStatus == "settlement") Color.Green else if (tx.transactionStatus == "pending") Color(0xFFFFA000) else Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}
