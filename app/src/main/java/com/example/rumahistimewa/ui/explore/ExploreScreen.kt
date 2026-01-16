package com.example.rumahistimewa.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rumahistimewa.ui.components.VillaCard
import com.example.rumahistimewa.ui.theme.RedPrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel(),
    onVillaClick: (String) -> Unit
) {
    val villas by viewModel.villas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val wishlistViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.rumahistimewa.ui.wishlist.WishlistViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return com.example.rumahistimewa.ui.wishlist.WishlistViewModel(
                    com.example.rumahistimewa.data.repository.WishlistRepository(com.example.rumahistimewa.data.remote.RetrofitClient.api)
                ) as T
            }
        }
    )
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsState()
    val isLoggedIn by com.example.rumahistimewa.util.UserSession.isLoggedIn.collectAsState()
    val wishlistedIdsFromApi = remember(wishlistItems) { wishlistItems.map { it.id }.toSet() }
    val optimisticWishlistedIdsState = remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            wishlistViewModel.fetchWishlist()
        } else {
            optimisticWishlistedIdsState.value = emptySet()
        }
    }

    LaunchedEffect(wishlistedIdsFromApi) {
        optimisticWishlistedIdsState.value = wishlistedIdsFromApi
    }
    
    // Sort logic
    var sortOption by remember { mutableStateOf<String?>(null) }
    
    val filteredVillas = remember(villas, searchQuery, sortOption) {
        var result = villas.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.location.contains(searchQuery, ignoreCase = true) 
        }
        
        when (sortOption) {
            "highest" -> result = result.sortedByDescending { it.price.filter { c -> c.isDigit() }.toLongOrNull() ?: 0L }
            "lowest" -> result = result.sortedBy { it.price.filter { c -> c.isDigit() }.toLongOrNull() ?: Long.MAX_VALUE }
        }
        result
    }

    val sheetState = rememberModalBottomSheetState()

    if (showFilterDialog) {
        ModalBottomSheet(
            onDismissRequest = { showFilterDialog = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
             Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Filter & Sort", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    "Sort by Price:", 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { sortOption = "lowest"; showFilterDialog = false }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = sortOption == "lowest", onClick = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lowest Price")
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { sortOption = "highest"; showFilterDialog = false }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = sortOption == "highest", onClick = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Highest Price")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { showFilterDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Apply Filter")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Scaffold(
        // Remove default TopBar
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Custom Header with Red Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RedPrimary)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        placeholder = { 
                            Text(
                                "Instant Cashback s.d Rp...", 
                                style = MaterialTheme.typography.bodyMedium, 
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            ) 
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search, 
                                contentDescription = "Search",
                                tint = Color.Gray
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Filter Icon (was List)
                     IconButton(onClick = { showFilterDialog = true }) {
                         Icon(
                            imageVector = Icons.Filled.Menu, 
                            contentDescription = "Filter",
                            tint = Color.White,
                             modifier = Modifier.size(24.dp)
                        )
                     }
                }
            }

            // List (with Pull Refresh)
            var isRefreshing by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.refresh()
                    scope.launch {
                        kotlinx.coroutines.delay(1000)
                        isRefreshing = false
                    }
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVillas) { villa ->
                        val isWishlisted = optimisticWishlistedIdsState.value.contains(villa.id)
                        VillaCard(
                            title = villa.title,
                            location = villa.location,
                            price = villa.price,
                            rating = villa.rating,
                            imageUrl = villa.imageUrl,
                            isWishlisted = isWishlisted,
                            onWishlistClick = {
                                if (!isLoggedIn) {
                                    android.widget.Toast.makeText(context, "Silakan login dulu", android.widget.Toast.LENGTH_SHORT).show()
                                    return@VillaCard
                                }

                                val villaIdInt = villa.id.toIntOrNull()
                                if (villaIdInt == null) return@VillaCard

                                if (isWishlisted) {
                                    optimisticWishlistedIdsState.value = optimisticWishlistedIdsState.value - villa.id
                                    wishlistViewModel.removeFromWishlist(villa.id)
                                } else {
                                    optimisticWishlistedIdsState.value = optimisticWishlistedIdsState.value + villa.id
                                    wishlistViewModel.addToWishlist(villaIdInt)
                                }
                            },
                            onClick = { onVillaClick(villa.id) }
                        )
                    }
                }
                
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = RedPrimary
                )
            }
        }
    }
}
