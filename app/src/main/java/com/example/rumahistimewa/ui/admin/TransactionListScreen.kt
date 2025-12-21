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
import com.example.rumahistimewa.ui.theme.RedPrimary

data class AdminTransaction(val id: String, val customer: String, val villa: String, val date: String, val amount: String, val status: String)

@Composable
fun TransactionListScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val transactions = listOf(
        AdminTransaction("TX001", "John Doe", "Villa Paradise", "10-12 Dec", "$200", "Success"),
        AdminTransaction("TX002", "Alice Wonder", "Mountain Retreat", "15-16 Dec", "$150", "Pending"),
        AdminTransaction("TX003", "Bob Builder", "Seaside Escape", "20-25 Dec", "$500", "Cancelled")
    )
    
    var selectedTransaction by remember { mutableStateOf<AdminTransaction?>(null) }

    if (selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { selectedTransaction = null },
            title = { Text("Transaction Details") },
            text = {
                Column {
                    Text("ID: ${selectedTransaction?.id}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Customer: ${selectedTransaction?.customer}")
                    Text("Villa: ${selectedTransaction?.villa}")
                    Text("Date: ${selectedTransaction?.date}")
                    Text("Amount: ${selectedTransaction?.amount}", color = RedPrimary, fontWeight = FontWeight.Bold)
                    Text("Status: ${selectedTransaction?.status}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Payment Method: Credit Card (Mock)")
                    Text("Timestamp: 2025-12-15 10:30 AM")
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
            }
        }
    }
}

@Composable
fun TransactionRow(tx: AdminTransaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(tx.villa, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(tx.amount, style = MaterialTheme.typography.bodySmall, color = RedPrimary)
        }
        Text(tx.customer, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
        Text(tx.date, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        
        Text(
            tx.status, 
            modifier = Modifier.weight(0.8f), 
            style = MaterialTheme.typography.bodySmall,
            color = if (tx.status == "Success") Color.Green else if (tx.status == "Cancelled") Color.Red else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}
