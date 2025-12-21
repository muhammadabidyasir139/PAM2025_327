package com.example.rumahistimewa.ui.mybooking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.components.VillaCard
import com.example.rumahistimewa.ui.theme.RedPrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Past")
    var bookings by remember { mutableStateOf<List<com.example.rumahistimewa.data.model.Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getMyBookings()
            if (response.isSuccessful) {
                bookings = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    val upcomingBookings = remember(bookings) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val now = System.currentTimeMillis()
        bookings.filter {
            val checkOutTime = sdf.parse(it.checkOut)?.time ?: 0L
            checkOutTime >= now
        }
    }
    
    val pastBookings = remember(bookings) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val now = System.currentTimeMillis()
        bookings.filter {
            val checkOutTime = sdf.parse(it.checkOut)?.time ?: 0L
            checkOutTime < now
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Booking", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = RedPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = RedPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            // List
            /*
            val pullRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()
            
            // Logic to trigger refresh
            /*
            // Logic to trigger refresh
            if (pullRefreshState.isRefreshing) {
                LaunchedEffect(Unit) {
                    // Trigger a refresh by toggling a state or calling a suspend function
                    // Here we can just reset isLoading to true which re-triggers the main LaunchedEffect if we structure it right
                    // But better: use a key.
                    // Actually, we need to manually call the API again here.
                    isLoading = true
                    try {
                        val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getMyBookings()
                        if (response.isSuccessful) {
                            bookings = response.body() ?: emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            }
            
            // Sync state
            LaunchedEffect(isLoading) {
               if (isLoading) pullRefreshState.startRefresh() else pullRefreshState.endRefresh()
            }
            */
            */

            if (isLoading) { // Initial load only
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = RedPrimary)
                 }
            } else {
                Box(
                     modifier = Modifier
                        .fillMaxSize()
                        // .nestedScroll(pullRefreshState.nestedScrollConnection)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val listToShow = if (selectedTabIndex == 0) upcomingBookings else pastBookings
                        
                        if (listToShow.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    // Illustration
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Outlined.DateRange, // Placeholder for Folder/Sleep
                                        contentDescription = null,
                                        tint = RedPrimary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(120.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // Title
                                    Text(
                                        text = "You don't have any active bookings",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Subtitle
                                    Text(
                                        text = "Log In or Register to manage your booking with ease. Use the email you used when booking to refund, reschedule, or view your past bookings.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { /* TODO: Navigate to Login */ },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, RedPrimary),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Log In")
                                        }
                                        
                                        Button(
                                            onClick = { /* TODO: Navigate to Register */ },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Register")
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(48.dp))
                                    
                                    // Bottom Section
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = androidx.compose.ui.Alignment.Start
                                    ) {
                                        Text(
                                            text = "All Purchase & Refund Activities",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Card(
                                            onClick = { /* TODO */ },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.List,
                                                    contentDescription = null,
                                                    tint = RedPrimary
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = "Your purchase list",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(listToShow.size) { index ->
                                val booking = listToShow[index]
                                VillaCard(
                                    title = booking.villa?.name ?: "Unknown Villa",
                                    location = booking.villa?.location ?: "-",
                                    price = booking.status, // Show status here
                                    rating = 4.5, // Default/Mock rating
                                    imageUrl = booking.villa?.photos?.firstOrNull(),
                                    onClick = {}
                                )
                            }
                        }
                    }
                    
                    /*
                    androidx.compose.material3.pulltorefresh.PullToRefreshContainer(
                        state = pullRefreshState,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter),
                        containerColor = Color.White,
                        contentColor = RedPrimary
                    )
                    */
                }
            }
        }
    }
}
