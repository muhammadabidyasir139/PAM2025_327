package com.example.rumahistimewa.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun LoginRequiredScreen(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Login Required",
            tint = com.example.rumahistimewa.ui.theme.RedPrimary,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Login Required",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Anda harus login terlebih dahulu untuk mengakses halaman ini.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Login Sekarang")
        }
    }
}
