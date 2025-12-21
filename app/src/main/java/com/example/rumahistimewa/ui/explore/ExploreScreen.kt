package com.example.rumahistimewa.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rumahistimewa.ui.components.VillaCard
import com.example.rumahistimewa.ui.theme.RedPrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel(),
    onVillaClick: (String) -> Unit
) {
    val villas by viewModel.villas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        // Remove default TopBar
        containerColor = Color.White
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
                    .padding(16.dp)
            ) {
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar
                    androidx.compose.foundation.background(
                        color = Color.White,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .background(Color.White, androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
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
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Icons
                     Icon(
                        imageVector = Icons.Default.Info, // Placeholder for "%"
                        contentDescription = "Promo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                     Icon(
                        imageVector = Icons.Default.notifications, // Placeholder for Chat
                        contentDescription = "Chat",
                        tint = Color.White,
                         modifier = Modifier.size(24.dp)
                    )
                     Spacer(modifier = Modifier.width(12.dp))
                     Icon(
                        imageVector = Icons.Default.List, // Placeholder for Receipt
                        contentDescription = "Orders",
                        tint = Color.White,
                         modifier = Modifier.size(24.dp)
                    )
                }
            }

            // List
            val pullRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()
            
            // Logic handled by LaunchedEffect(Unit) in ViewModel refresh call technically, 
            // but we need to trigger it from UI state changes if using pullRefreshState
            
            if (pullRefreshState.isRefreshing) {
                LaunchedEffect(Unit) {
                    viewModel.refresh()
                }
            }

            LaunchedEffect(isLoading) {
                if (isLoading) {
                    pullRefreshState.startRefresh()
                } else {
                    pullRefreshState.endRefresh()
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(villas) { villa ->
                        VillaCard(
                            title = villa.title,
                            location = villa.location,
                            price = villa.price,
                            rating = villa.rating,
                            imageUrl = villa.imageUrl,
                            onClick = { onVillaClick(villa.id) }
                        )
                    }
                }
                
                androidx.compose.material3.pulltorefresh.PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = Color.White,
                    contentColor = RedPrimary
                )
            }
        }
    }
}
