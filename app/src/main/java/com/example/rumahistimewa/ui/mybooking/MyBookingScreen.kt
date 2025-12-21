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
            val pullRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()
            
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

            if (isLoading && !pullRefreshState.isRefreshing) { // Initial load only
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = RedPrimary)
                 }
            } else {
                Box(
                     modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(pullRefreshState.nestedScrollConnection)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val listToShow = if (selectedTabIndex == 0) upcomingBookings else pastBookings
                        
                        if (listToShow.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Text("No bookings found", color = Color.Gray)
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
                    
                    androidx.compose.material3.pulltorefresh.PullToRefreshContainer(
                        state = pullRefreshState,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter),
                        containerColor = Color.White,
                        contentColor = RedPrimary
                    )
                }
            }
        }
    }
}
