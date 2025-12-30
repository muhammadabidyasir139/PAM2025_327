package com.example.rumahistimewa.ui.profile

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rumahistimewa.data.model.ProfileResponse
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.data.repository.ProfileRepository
import coil.compose.AsyncImage
import com.example.rumahistimewa.ui.theme.RedPrimary
import androidx.lifecycle.ViewModelProvider

data class MenuItem(val title: String, val onClick: () -> Unit)
data class MenuCategory(val title: String, val items: List<MenuItem>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val repository = ProfileRepository(RetrofitClient.api)
    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(repository) as T
            }
        }
    )

    val profile by viewModel.profileState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by com.example.rumahistimewa.util.UserSession.isLoggedIn.collectAsState() 

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchProfile()
        }
    }

    val categories = listOf(
        MenuCategory("Account", listOf(
            MenuItem("Edit Profile") { onNavigate("profile_edit") },
            MenuItem("Renew Password") { onNavigate("profile_password") }
        )),
        MenuCategory("History", listOf(
            MenuItem("My Bookings") { onNavigate("my_booking") }, 
            MenuItem("Wishlist") { onNavigate("wishlist") }, 
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
        
        if (isLoggedIn) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    if (isLoading && profile == null) {
                         Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                             CircularProgressIndicator(color = RedPrimary)
                         }
                    } else if (profile != null) {
                        ProfileHeader(profile!!)
                    } else if (error != null) {
                        Text(
                            text = error ?: "Unknown error",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
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
        } else {
             com.example.rumahistimewa.ui.components.EmptyState(
                illustration = Icons.Default.AccountCircle,
                title = "Login Required",
                subtitle = "Anda harus login terlebih dahulu untuk mengakses halaman ini.",
                onLoginClick = { onNavigate("login") },
                onRegisterClick = { onNavigate("register") },
                showPurchaseList = false
            )
        }
    }
}

@Composable
fun ProfileHeader(profile: ProfileResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (profile.photo != null) {
             AsyncImage(
                model = profile.photo,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = null, // You might want a placeholder painter here
                error = null // And an error painter
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                tint = RedPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            if (profile.phone.isNotEmpty()) {
                 Text(
                    text = profile.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
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

