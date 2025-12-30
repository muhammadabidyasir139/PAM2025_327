package com.example.rumahistimewa.ui.wishlist

import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun WishlistScreen() {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.rumahistimewa.ui.wishlist.WishlistViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return com.example.rumahistimewa.ui.wishlist.WishlistViewModel(
                    com.example.rumahistimewa.data.repository.WishlistRepository(com.example.rumahistimewa.data.remote.RetrofitClient.api)
                ) as T
            }
        }
    )

    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoggedIn by com.example.rumahistimewa.util.UserSession.isLoggedIn.collectAsState()
    
    // Fetch data when logged in
    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchWishlist()
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Wishlist", color = androidx.compose.ui.graphics.Color.White) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = com.example.rumahistimewa.ui.theme.RedPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoggedIn) {
                // Pull Refresh Logic
                var isRefreshing by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.fetchWishlist()
                        scope.launch {
                            kotlinx.coroutines.delay(1000)
                            isRefreshing = false
                        }
                    }
                )
                
                Box(
                     modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    if (isLoading && wishlistItems.isEmpty() && !isRefreshing) {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            androidx.compose.material3.CircularProgressIndicator(color = com.example.rumahistimewa.ui.theme.RedPrimary)
                         }
                    } else if (wishlistItems.isEmpty() && !isRefreshing) {
                         com.example.rumahistimewa.ui.components.EmptyState(
                            illustration = androidx.compose.material.icons.Icons.Default.Favorite,
                            title = "Your wishlist is empty",
                            subtitle = "Explore our villas and save your favorites here.",
                            onLoginClick = { },
                            onRegisterClick = { },
                            showPurchaseList = false,
                            showLoginButtons = false
                        )
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                        ) {
                            items(wishlistItems.size) { index ->
                                val item = wishlistItems[index]
                                com.example.rumahistimewa.ui.components.VillaCard(
                                    title = item.name,
                                    location = item.location,
                                    price = "Rp ${item.price.toInt()}",
                                    rating = 4.8, 
                                    imageUrl = item.photos.firstOrNull(),
                                    isWishlisted = true,
                                    onWishlistClick = { viewModel.removeFromWishlist(item.id) },
                                    onClick = { /* Navigate logic needed here really */ }
                                )
                            }
                        }
                    }
                    
                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        contentColor = com.example.rumahistimewa.ui.theme.RedPrimary
                    )
                }
            } else {
                com.example.rumahistimewa.ui.components.EmptyState(
                    illustration = androidx.compose.material.icons.Icons.Default.Favorite,
                    title = "Login Required",
                    subtitle = "Log in to save your dream villas and view them from any device.",
                    onLoginClick = { /* Handled by NavHost */ }, 
                    onRegisterClick = { /* Handled by NavHost */ },
                    showPurchaseList = false
                )
            }
        }
    }
}
