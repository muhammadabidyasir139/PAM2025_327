package com.example.rumahistimewa.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailVillaScreen(
    villaId: String?,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit
) {
    var villa by remember { mutableStateOf<com.example.rumahistimewa.data.model.Villa?>(null) }
    
    LaunchedEffect(villaId) {
        if (villaId != null) {
            try {
                val response = com.example.rumahistimewa.data.remote.RetrofitClient.api.getVillaDetail(villaId)
                if (response.isSuccessful) {
                    villa = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(villa?.name ?: "Detail Villa", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    var isFavorite by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isFavorite) RedSecondary else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedSecondary)
            ) {
                Text("Book Now")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF121212)) // Dark theme background
        ) {
            // Placeholder Image or Real Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.Gray)
            ) {
                 // If I had Coil, I would use it here.
                 // Image(painter = rememberAsyncImagePainter(villa?.photos?.firstOrNull()), ...)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = villa?.name ?: "Loading...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = RedSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(villa?.location ?: "Unknown Location", color = Color.LightGray)
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                    Text("4.8 (120 Reviews)", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${villa?.price?.toInt() ?: 0} / night",
                    style = MaterialTheme.typography.titleMedium,
                    color = RedSecondary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Description", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    villa?.description ?: "No description available.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Amenities, Map etc.
            }
        }
    }
}
