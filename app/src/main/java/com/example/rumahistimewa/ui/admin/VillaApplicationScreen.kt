package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun VillaApplicationScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var applications = remember { mutableStateListOf<com.example.rumahistimewa.data.model.Villa>() }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getAdminVillas()
            if (response.isSuccessful) {
                val allVillas = response.body() ?: emptyList()
                applications.clear()
                applications.addAll(allVillas.filter { it.status == "pending" })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    AdminLayout(
        title = "Villa Applications",
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
                        Text("Villa Name", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.titleSmall)
                        Text("Owner", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Text("Date", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Text("Actions", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()

                    LazyColumn {
                        items(applications) { app ->
                            ApplicationRow(
                                app = app, 
                                onApprove = { 
                                    scope.launch {
                                        try {
                                            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.approveVilla(app.id)
                                            if (response.isSuccessful) {
                                                applications.remove(app)
                                                android.widget.Toast.makeText(context, "Application Approved", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.widget.Toast.makeText(context, "Approve failed", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            android.widget.Toast.makeText(context, "Network error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onReject = { 
                                    scope.launch {
                                        try {
                                            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.deleteVillaAdmin(app.id)
                                            if (response.isSuccessful) {
                                                applications.remove(app)
                                                android.widget.Toast.makeText(context, "Application Rejected", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.widget.Toast.makeText(context, "Reject failed", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            android.widget.Toast.makeText(context, "Network error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                    
                    if (applications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No pending applications", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationRow(app: com.example.rumahistimewa.data.model.Villa, onApprove: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(app.name, style = MaterialTheme.typography.bodyMedium)
            Text(app.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(app.owner?.name ?: "ID: ${app.ownerId.take(5)}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text("-", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        
        Row(modifier = Modifier.weight(0.8f)) {
            IconButton(onClick = onApprove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.Green)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onReject, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Reject", tint = RedPrimary)
            }
        }
    }
}
