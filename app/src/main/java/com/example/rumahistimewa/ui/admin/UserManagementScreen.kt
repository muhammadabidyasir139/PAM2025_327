package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.rumahistimewa.ui.theme.RedSecondary
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.data.model.User

@Composable
fun UserManagementScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var users = remember { mutableStateListOf<User>() }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = RetrofitClient.api.getUsers()
            if (response.isSuccessful) {
                users.clear()
                users.addAll(response.body().orEmpty())
            } else {
                android.widget.Toast.makeText(
                    context,
                    "Failed: ${response.code()} - ${response.message()}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(
                context,
                "Network error: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } finally {
            isLoading = false
        }
    }

    AdminLayout(
        title = "User Management",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedSecondary)
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Name", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.titleSmall)
                        Text("Email", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.titleSmall)
                        Text("Role", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.titleSmall)
                        Text("Status", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()

                    LazyColumn {
                        items(users) { user ->
                            UserRow(user = user, onToggleStatus = {
                                scope.launch {
                                    val currentStatus = user.status ?: "active"
                                    val newStatus = if (currentStatus == "active") "suspended" else "active"
                                    
                                    // Optimistic update
                                    val index = users.indexOf(user)
                                    if (index != -1) {
                                        users[index] = user.copy(status = newStatus)
                                    }
                                    
                                    try {
                                        val response = RetrofitClient.api.updateUserStatus(
                                            user.id,
                                            mapOf("status" to newStatus)
                                        )
                                        if (!response.isSuccessful) {
                                            // Revert if failed
                                            if (index != -1) users[index] = user // Revert to old
                                            android.widget.Toast.makeText(context, "Failed to update status", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        if (index != -1) users[index] = user // Revert
                                        e.printStackTrace()
                                    }
                                }
                            })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(user: User, onToggleStatus: () -> Unit) {
    val isActive = user.status == "active"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.name, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodyMedium)
        Text(user.email ?: "No Email", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
        Text(user.role, modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = onToggleStatus,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) RedSecondary else Color.Gray
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(if (isActive) "Active" else "Suspended", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
