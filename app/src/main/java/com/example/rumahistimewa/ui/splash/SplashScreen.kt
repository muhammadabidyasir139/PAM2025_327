package com.example.rumahistimewa.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds delay
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        // You can replace this Text with an Image/Icon later
        Text(
            text = "Rumah Istimewa",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White
        )
    }
}
