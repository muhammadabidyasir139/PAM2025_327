package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.R
import com.example.rumahistimewa.ui.theme.RedPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLayout(
    title: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AdminSidebar(
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(
                    title = title,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                AdminFooter()
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(title: String, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = { /* Navigate to Home */ }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
            }
            IconButton(onClick = { /* Navigate to Profile */ }) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
    )
}

@Composable
fun AdminSidebar(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Branding: Only Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for Logo - Using Icon for now as no logo resource is guaranteed
            Icon(
                imageVector = Icons.Default.Home, 
                contentDescription = "Logo", 
                modifier = Modifier.size(64.dp),
                tint = RedPrimary
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        NavigationItem("Dashboard", Icons.Default.Home) { onNavigate("home_admin") }
        NavigationItem("User Management", Icons.Default.Person) { onNavigate("admin_users") }
        NavigationItem("Villa Management", Icons.Default.Home) { onNavigate("admin_villas") }
        NavigationItem("Transaction List", Icons.Default.List) { onNavigate("admin_transactions") }
        NavigationItem("Revenue Report", Icons.Default.DateRange) { onNavigate("admin_revenue") }
        NavigationItem("Villa Applications", Icons.Default.CheckCircle) { onNavigate("admin_villa_applications") }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        
        NavigationItem("Logout", Icons.Default.ExitToApp) { onLogout() }
    }
}

@Composable
fun NavigationItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, contentDescription = null) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun AdminFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE)) // Light Gray
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "© 2025 Muhammad Abid",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
