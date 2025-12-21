package com.example.rumahistimewa.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

data class MenuItem(val title: String, val onClick: () -> Unit)
data class MenuCategory(val title: String, val items: List<MenuItem>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val categories = listOf(
        MenuCategory("Account", listOf(
            MenuItem("Edit Profile") { onNavigate("profile_edit") },
            MenuItem("Renew Password") { onNavigate("profile_password") }
        )),
        MenuCategory("History", listOf(
            MenuItem("My Bookings") { onNavigate("my_booking") }, // This might need special handling if it's a tab
            MenuItem("Wishlist") { onNavigate("wishlist") }, // This might need special handling if it's a tab
            MenuItem("Transaction History") { onNavigate("profile_transactions") }
        )),
        MenuCategory("Support & Settings", listOf(
            MenuItem("Help Center / FAQ") { onNavigate("profile_help") },
            MenuItem("Contact Us") { onNavigate("profile_contact") }
        )),
        MenuCategory("Host / Owner", listOf(
            MenuItem("Villa Submission") { onNavigate("villa_submission") },
            MenuItem("Booking List") { onNavigate("owner_bookings") },
            MenuItem("Income / Revenue") { onNavigate("income_revenue") }
        )),
        MenuCategory("Legal", listOf(
            MenuItem("Terms of Service") { onNavigate("profile_terms") },
            MenuItem("Privacy Policy") { onNavigate("profile_privacy") }
        ))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                ProfileHeader()
                HorizontalDivider(thickness = 8.dp, color = Color.LightGray.copy(alpha = 0.2f))
            }

            // Categories
            items(categories) { category ->
                CategorySection(category)
                HorizontalDivider(thickness = 8.dp, color = Color.LightGray.copy(alpha = 0.2f))
            }

            // Logout
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) {
                        Text("Log Out")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            tint = RedPrimary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "John Doe",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "john.doe@example.com",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CategorySection(category: MenuCategory) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        category.items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .clickable { item.onClick() }
                    .background(Color.White) // Ensure background is set
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
