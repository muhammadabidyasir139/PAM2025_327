package com.example.rumahistimewa.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DetailVillaScreen(
    villaId: String?,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit
) {
    var villa by remember { mutableStateOf<com.example.rumahistimewa.data.model.Villa?>(null) }
    
    LaunchedEffect(villaId) {
        if (villaId != null) {
            try {
                val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getVillaDetail(villaId)
                if (response.isSuccessful) {
                    villa = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var currentMonth by remember { mutableStateOf(java.time.YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<java.time.LocalDate?>(null) }
    
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.rumahistimewa.ui.wishlist.WishlistViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return com.example.rumahistimewa.ui.wishlist.WishlistViewModel(
                    com.example.rumahistimewa.data.repository.WishlistRepository(com.example.rumahistimewa.data.remote.RetrofitClient.api)
                ) as T
            }
        }
    )

    val transactionViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.rumahistimewa.ui.profile.transaction.TransactionViewModel>()
    val paymentUrl by transactionViewModel.paymentUrl.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current



    // Optimistic UI State
    val isWishlistedVM by viewModel.isWishlisted(villaId ?: "").collectAsState()
    var isWishlistedLocal by remember { mutableStateOf(false) }

    LaunchedEffect(isWishlistedVM) {
        isWishlistedLocal = isWishlistedVM
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(villa?.name ?: "Detail Villa", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (villa != null) {
                            // Optimistic Update
                            isWishlistedLocal = !isWishlistedLocal
                            
                            if (isWishlistedLocal) {
                                viewModel.addToWishlist(villa!!.id)
                            } else {
                                viewModel.removeFromWishlist(villa!!.id)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isWishlistedLocal) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlistedLocal) RedSecondary else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                        onBookClick()
                },
                // Keep enabled to show toast if date not selected, or disable if preferred. 
                // Requests said "after pilih tanggal booking tidak dapat diklik" which implies disabled UNTIL date selected.
                enabled = selectedDate != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedSecondary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Book Now")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            // Placeholder Image or Real Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.Gray)
            ) {
                 if (villa?.photos?.isNotEmpty() == true) {
                     coil.compose.AsyncImage(
                         model = villa!!.photos.first(),
                         contentDescription = "Villa Image",
                         contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                         modifier = Modifier.fillMaxSize()
                     )
                 }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = villa?.name ?: "Loading...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = RedSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(villa?.location ?: "Unknown Location", color = Color.LightGray)
                    
                    // Removed Reviews Section as requested
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${villa?.price?.toInt() ?: 0} / night",
                    style = MaterialTheme.typography.titleMedium,
                    color = RedSecondary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Description", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    villa?.description ?: "No description available.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Availability Calendar
                Text("Availability", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                // Simple Calendar Implementation
                com.example.rumahistimewa.ui.components.SimpleCalendar(
                    yearMonth = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date },
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                )
            }
        }
    }
}

