package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun VillaManagementScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var villas = remember { mutableStateListOf<com.example.rumahistimewa.data.model.Villa>() }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getAdminVillas()
            if (response.isSuccessful) {
                val allVillas = response.body() ?: emptyList()
                villas.clear()
                // Show approved/rejected here, as Pending is in another screen
                villas.addAll(allVillas.filter { it.status != "pending" })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    AdminLayout(
        title = "Villa Management",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // Add Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onNavigate("admin_villa_form") },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Villa")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Name", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.titleSmall)
                        Text("Location", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Text("Owner", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Text("Actions", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()

                    LazyColumn {
                        items(villas) { villa ->
                            VillaRow(
                                villa = villa, 
                                onDelete = {
                                    scope.launch {
                                        try {
                                             val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.deleteVillaAdmin(villa.id)
                                             if (response.isSuccessful) {
                                                 villas.remove(villa)
                                                 android.widget.Toast.makeText(context, "Villa deleted", android.widget.Toast.LENGTH_SHORT).show()
                                             } else {
                                                 android.widget.Toast.makeText(context, "Delete failed", android.widget.Toast.LENGTH_SHORT).show()
                                             }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }, 
                                onEdit = { onNavigate("admin_villa_form/${villa.id}") },
                                onView = { onNavigate("admin_villa_detail/${villa.id}") }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VillaRow(villa: com.example.rumahistimewa.data.model.Villa, onDelete: () -> Unit, onEdit: () -> Unit, onView: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Villa") },
            text = { Text("Are you sure you want to delete ${villa.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(villa.name, style = MaterialTheme.typography.bodyMedium)
        }
        Text(villa.location, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(villa.owner?.name ?: "ID: ${villa.ownerId.take(5)}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        
        Row(modifier = Modifier.weight(1.0f)) {
            IconButton(onClick = onView, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Info, contentDescription = "View", tint = Color.Blue)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}
