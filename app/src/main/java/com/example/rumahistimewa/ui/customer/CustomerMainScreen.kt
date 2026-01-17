package com.example.rumahistimewa.ui.customer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rumahistimewa.ui.explore.ExploreScreen
import com.example.rumahistimewa.ui.mybooking.MyBookingScreen
import com.example.rumahistimewa.ui.profile.ProfileScreen
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.wishlist.WishlistScreen

@Composable
fun CustomerMainScreen(
    onVillaClick: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfileDetail: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Explore", "Wishlist", "Booking", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Favorite,
        Icons.Default.DateRange,
        Icons.Default.Person
    )
    
    val isLoggedIn by com.example.rumahistimewa.util.UserSession.isLoggedIn.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedPrimary,
                            selectedTextColor = RedPrimary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // We apply the padding from the bottom bar to the content
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> ExploreScreen(
                    onVillaClick = onVillaClick
                )
                1 -> {
                    WishlistScreen(
                        onVillaClick = onVillaClick,
                        onLoginClick = onLoginClick
                    )
                }
                2 -> {
                    MyBookingScreen(
                        onLoginClick = onLoginClick
                    )
                }
                3 -> {
                    ProfileScreen(
                        onLogout = onLogout,
                        onNavigate = onNavigateToProfileDetail
                    )
                }
            }
        }
    }
}
