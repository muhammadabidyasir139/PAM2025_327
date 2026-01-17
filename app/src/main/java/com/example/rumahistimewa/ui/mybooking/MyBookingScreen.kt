package com.example.rumahistimewa.ui.mybooking

import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.components.VillaCard
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingScreen(
    onBackClick: () -> Unit = {},
    onBookingClick: (Int) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = onLoginClick
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Past")
    var bookings by remember { mutableStateOf<List<com.example.rumahistimewa.data.model.MyBookingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var villasById by remember { mutableStateOf<Map<Int, com.example.rumahistimewa.data.model.Villa>>(emptyMap()) }
    
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
             villasById = emptyMap()
             isLoading = false
        }
    }

    LaunchedEffect(bookings) {
        if (bookings.isEmpty()) {
            villasById = emptyMap()
            return@LaunchedEffect
        }

        val ids = bookings.map { it.villaId }.distinct()
        val results = kotlinx.coroutines.coroutineScope {
            ids.map { id ->
                async {
                    val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getVillaDetail(id.toString())
                    if (response.isSuccessful) response.body() else null
                }
            }.awaitAll()
        }

        villasById = results.filterNotNull().associateBy { it.id.toIntOrNull() ?: -1 }.filterKeys { it != -1 }
    }

    fun parseDateMillis(value: String): Long? {
        val trimmed = value.trim()
        val parsedInstant = runCatching { java.time.Instant.parse(trimmed).toEpochMilli() }.getOrNull()
        if (parsedInstant != null) return parsedInstant

        val parsedDateOnly = runCatching {
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(trimmed)?.time
        }.getOrNull()
        if (parsedDateOnly != null) return parsedDateOnly

        val parsedIso = runCatching {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            sdf.parse(trimmed)?.time
        }.getOrNull()
        return parsedIso
    }
    
    val upcomingBookings = remember(bookings) {
        val now = System.currentTimeMillis()
        bookings.filter {
            val checkOutTime = parseDateMillis(it.checkOut) ?: Long.MAX_VALUE
            checkOutTime >= now
        }
    }
    
    val pastBookings = remember(bookings) {
        val now = System.currentTimeMillis()
        bookings.filter {
            val checkOutTime = parseDateMillis(it.checkOut) ?: Long.MAX_VALUE
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
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                    onLoginClick = onLoginClick,
                                    onRegisterClick = onRegisterClick,
                                    showLoginButtons = !isLoggedIn,
                                    showPurchaseList = isLoggedIn
                                )
                            }
                        } else {
                            items(listToShow.size) { index ->
                                val booking = listToShow[index]
                                val villa = villasById[booking.villaId]
                                val villaName = booking.villaName ?: villa?.name ?: "Villa #${booking.villaId}"
                                val location = booking.location ?: villa?.location ?: "-"
                                val statusRaw = booking.bookingStatus ?: "-"
                                val totalAmount = booking.bookingTotalAmount ?: 0L
                                val bookingId = booking.bookingId ?: booking.bookingIdAlt
                                Card(
                                    onClick = { if (bookingId != null) onBookingClick(bookingId) },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = villaName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = location,
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
                                                val statusText = when (statusRaw) {
                                                    "waiting_payment" -> "Waiting Payment"
                                                    else -> statusRaw
                                                }
                                                Text(
                                                    text = "Status: $statusText",
                                                    color = if (statusRaw == "waiting_payment") RedPrimary else Color.Gray,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    text = "Rp ${
                                                        java.text.NumberFormat.getIntegerInstance(
                                                            java.util.Locale("id", "ID")
                                                        ).format(totalAmount)
                                                    }",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = RedPrimary
                                                )
                                            }
                                            
                                            val context = androidx.compose.ui.platform.LocalContext.current
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                val paymentUrl = sanitizeUrl(booking.payment?.redirectUrl)
                                                val isWaitingPayment = statusRaw == "waiting_payment"
                                                val isPaid = statusRaw == "success"
                                                if (isWaitingPayment && paymentUrl != null) {
                                                    Button(
                                                        onClick = {
                                                            val intent = android.content.Intent(
                                                                android.content.Intent.ACTION_VIEW,
                                                                android.net.Uri.parse(paymentUrl)
                                                            )
                                                            context.startActivity(intent)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = RedSecondary),
                                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                                    ) {
                                                        Text("Pay Now", fontSize = 12.sp)
                                                    }
                                                } else if (isPaid) {
                                                    Button(
                                                        onClick = {},
                                                        enabled = false,
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color.Gray,
                                                            disabledContainerColor = Color.Gray
                                                        ),
                                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                                    ) {
                                                        Text("Paid", fontSize = 12.sp, color = Color.White)
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        val uri = android.net.Uri.parse("https://wa.link/ijl6qp")
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                                                            setPackage("com.whatsapp")
                                                        }
                                                        try {
                                                            context.startActivity(intent)
                                                        } catch (e: android.content.ActivityNotFoundException) {
                                                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                                ) {
                                                    Text("WhatsApp", fontSize = 12.sp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBookingScreen(
    bookingId: Int,
    onBackClick: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var booking by remember { mutableStateOf<com.example.rumahistimewa.data.model.BookingDetail?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookingId) {
        isLoading = true
        errorMessage = null
        try {
            val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getBookingDetail(bookingId)
            if (response.isSuccessful) {
                val body = response.body()
                booking = body?.booking ?: body?.data
                if (booking == null) {
                    errorMessage = body?.message ?: "Booking not found"
                }
            } else {
                errorMessage = response.message()
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Detail", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedPrimary)
                    }
                }

                booking != null -> {
                    val b = booking!!
                    val resolvedStatus = b.status ?: b.bookingStatus ?: "-"
                    val resolvedCheckIn = b.checkIn ?: b.checkInAlt ?: "-"
                    val resolvedCheckOut = b.checkOut ?: b.checkOutAlt ?: "-"
                    val resolvedTotal = b.totalAmount ?: b.totalAmountAlt ?: b.bookingTotalAmount ?: 0L
                    val formattedTotal = remember(resolvedTotal) {
                        java.text.NumberFormat.getIntegerInstance(java.util.Locale("id", "ID")).format(resolvedTotal)
                    }
                    val resolvedVillaName = b.villaName ?: b.villa?.name ?: run {
                        val idText = b.villaId?.toString() ?: "-"
                        "Villa #$idText"
                    }
                    val resolvedLocation = b.location ?: b.villa?.location ?: "-"
                    val paymentUrl = sanitizeUrl(b.payment?.redirectUrl)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(resolvedVillaName, style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                Text(resolvedLocation, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Booking ID: $bookingId", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                Text("Status: $resolvedStatus", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                Text("Check-in: $resolvedCheckIn", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                Text("Check-out: $resolvedCheckOut", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                Text(
                                    text = "Total: Rp $formattedTotal",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = RedPrimary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val uri = android.net.Uri.parse("https://wa.link/ijl6qp")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.whatsapp")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: android.content.ActivityNotFoundException) {
                                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                            ) {
                                Text("WhatsApp")
                            }

                            Button(
                                onClick = {
                                    if (paymentUrl != null) {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse(paymentUrl)
                                        )
                                        context.startActivity(intent)
                                    }
                                },
                                enabled = paymentUrl != null,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = RedSecondary)
                            ) {
                                Text("Payment")
                            }
                        }
                    }
                }

                else -> {
                    val message = errorMessage ?: "Booking not found"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(message, color = Color.Gray)
                        Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}

private fun sanitizeUrl(url: String?): String? {
    val cleaned = url?.trim()?.trim('`')?.trim()
    return cleaned?.takeIf { it.isNotEmpty() }
}
