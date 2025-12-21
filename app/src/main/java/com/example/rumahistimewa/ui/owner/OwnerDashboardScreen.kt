package com.example.rumahistimewa.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Villa */ },
                containerColor = RedPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Villa")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("My Villas", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            
            // List of Villas (Placeholder)
            LazyColumn {
                 item {
                     Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text("Executive Suite Villa", style = MaterialTheme.typography.titleMedium)
                             Text("Status: Approved", color = Color.Green)
                         }
                     }
                 }
                 item {
                     Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text("Sunset Private Pool", style = MaterialTheme.typography.titleMedium)
                             Text("Status: Pending", color = Color(0xFFFFC107))
                         }
                     }
                 }
            }
        }
    }
}
