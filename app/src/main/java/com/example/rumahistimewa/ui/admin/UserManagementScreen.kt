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

data class AdminUser(val id: String, val name: String, val email: String, val role: String, val isActive: Boolean)

@Composable
fun UserManagementScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var users = remember { mutableStateListOf<com.example.rumahistimewa.data.model.User>() }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getUsers()
            if (response.isSuccessful) {
                users.clear()
                users.addAll(response.body() ?: emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                                    val newStatus = !user.isActive
                                    // Optimistic update
                                    val index = users.indexOf(user)
                                    if (index != -1) {
                                        users[index] = user.copy(isActive = newStatus)
                                    }
                                    
                                    try {
                                        val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.updateUserStatus(
                                            user.id,
                                            mapOf("isActive" to newStatus.toString())
                                        )
                                        if (!response.isSuccessful) {
                                            // Revert if failed
                                            users[index] = user // Revert to old
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
fun UserRow(user: com.example.rumahistimewa.data.model.User, onToggleStatus: () -> Unit) {
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
                    containerColor = if (user.isActive) RedSecondary else Color.Gray
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(if (user.isActive) "Active" else "Suspended", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
