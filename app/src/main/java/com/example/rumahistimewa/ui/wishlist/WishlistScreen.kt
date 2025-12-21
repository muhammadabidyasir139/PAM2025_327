package com.example.rumahistimewa.ui.wishlist

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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen() {
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
        /*
        val pullRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()
        var isRefreshing by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        /*
        if (pullRefreshState.isRefreshing) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                isRefreshing = true
                kotlinx.coroutines.delay(1000) // Simulate network delay
                isRefreshing = false
            }
        }

        androidx.compose.runtime.LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullRefreshState.startRefresh()
            } else {
                pullRefreshState.endRefresh()
            }
        }
        */
        */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = "Your Dream Villas",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(3) { index ->
                    com.example.rumahistimewa.ui.components.VillaCard(
                        title = "Wishlist Villa ${index + 1}",
                        location = "Bali",
                        price = "$${(index + 1) * 100} / Night",
                        rating = 4.5 + (index * 0.1),
                        imageUrl = null,
                        onClick = {}
                    )
                }
            }
            
            /*
            androidx.compose.material3.pulltorefresh.PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = androidx.compose.ui.graphics.Color.White,
                contentColor = com.example.rumahistimewa.ui.theme.RedPrimary
            )
            */
        }
    }
}
