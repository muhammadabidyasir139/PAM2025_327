package com.example.rumahistimewa.ui.profile.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextScreen(
    title: String,
    content: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Text(content, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    SimpleTextScreen(
        title = "Help Center",
        content = "This is the Help Center. Here you can find FAQs and guides on how to use the app.",
        onBackClick = onBackClick
    )
}

@Composable
fun ContactUsScreen(onBackClick: () -> Unit) {
    SimpleTextScreen(
        title = "Contact Us",
        content = "Contact us at support@rumahistimewa.com\nPhone: +62 812 3456 7890",
        onBackClick = onBackClick
    )
}

@Composable
fun TermsScreen(onBackClick: () -> Unit) {
    SimpleTextScreen(
        title = "Terms of Service",
        content = "These are the Terms of Service. By using this app, you agree to...",
        onBackClick = onBackClick
    )
}

@Composable
fun PrivacyScreen(onBackClick: () -> Unit) {
    SimpleTextScreen(
        title = "Privacy Policy",
        content = "We value your privacy. This policy explains how we handle your data...",
        onBackClick = onBackClick
    )
}
