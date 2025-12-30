package com.example.rumahistimewa.ui.mybooking

import androidx.compose.runtime.collectAsState

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
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Past")
    var bookings by remember { mutableStateOf<List<com.example.rumahistimewa.data.model.Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val isLoggedIn by com.example.rumahistimewa.util.UserSession.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
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
        } else {
             bookings = emptyList()
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
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                         Icon(
                             imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                             contentDescription = "Back",
                             tint = Color.White
                         )
                    }
                },
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
                                com.example.rumahistimewa.ui.components.EmptyState(
                                    illustration = androidx.compose.material.icons.Icons.Outlined.DateRange,
                                    title = if (isLoggedIn) "You don't have any active bookings" else "Login Required",
                                    subtitle = if (isLoggedIn) 
                                        "Explore our villas and make your first booking today!" 
                                    else 
                                        "Log In or Register to manage your booking with ease.",
                                    onLoginClick = { /* TODO: Navigate to Login */ },
                                    onRegisterClick = { /* TODO: Navigate to Register */ },
                                    showPurchaseList = !isLoggedIn,
                                    showLoginButtons = !isLoggedIn
                                )
                            }
                        } else {
                            items(listToShow.size) { index ->
                                val booking = listToShow[index]
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = booking.villaName ?: booking.villa?.name ?: "Villa Name",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = booking.location ?: booking.villa?.location ?: "Location",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Status: ${booking.status}",
                                                    color = if (booking.status == "waiting_payment") RedPrimary else Color.Gray,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    text = "Rp ${java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID")).format(booking.totalAmount)}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = RedPrimary
                                                )
                                            }
                                            
                                            if (booking.status == "waiting_payment" && booking.redirectUrl != null) {
                                                val context = androidx.compose.ui.platform.LocalContext.current
                                                Button(
                                                    onClick = {
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(booking.redirectUrl))
                                                        context.startActivity(intent)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                                    modifier = Modifier.height(36.dp)
                                                ) {
                                                    Text("Pay", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
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
