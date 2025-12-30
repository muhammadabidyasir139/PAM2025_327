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
import com.example.rumahistimewa.data.model.Booking
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.ui.theme.RedPrimary
import kotlinx.coroutines.launch

@Composable
fun TransactionListScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var transactions by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTransaction by remember { mutableStateOf<Booking?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAdminTransactions()
            if (response.isSuccessful) {
                transactions = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (selectedTransaction != null) {
        val tx = selectedTransaction!!
        AlertDialog(
            onDismissRequest = { selectedTransaction = null },
            title = { Text("Transaction Details") },
            text = {
                Column {
                    Text("ID: ${tx.id}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Customer: ${tx.user?.name ?: "User ${tx.userId}"}")
                    Text("Villa: ${tx.villaName ?: tx.villa?.name ?: "Villa ${tx.villaId}"}")
                    Text("CheckIn: ${tx.checkIn}")
                    Text("CheckOut: ${tx.checkOut}")
                    Text("Amount: Rp ${tx.totalAmount}", color = RedPrimary, fontWeight = FontWeight.Bold)
                    Text("Status: ${tx.status}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTransaction = null }) {
                    Text("Close")
                }
            }
        )
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
                            TransactionRow(tx = tx, onClick = { selectedTransaction = tx })
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
fun TransactionRow(tx: Booking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(tx.villaName ?: tx.villa?.name ?: "Villa ${tx.villaId}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Rp ${tx.totalAmount}", style = MaterialTheme.typography.bodySmall, color = RedPrimary)
        }
        Text(tx.user?.name ?: "User ${tx.userId}", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
        Text(tx.checkIn, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        
        Text(
            tx.status, 
            modifier = Modifier.weight(0.8f), 
            style = MaterialTheme.typography.bodySmall,
            color = if (tx.status == "success" || tx.status == "approved") Color.Green else if (tx.status == "pending") Color(0xFFFFA000) else Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}
