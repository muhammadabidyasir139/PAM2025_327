package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rumahistimewa.data.model.Villa
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVillaDetailScreen(
    villaId: String,
    onBackClick: () -> Unit
) {
    var villa by remember { mutableStateOf<Villa?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(villaId) {
        try {
            val response = RetrofitClient.api.getVillaDetail(villaId)
            if (response.isSuccessful) {
                villa = response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Villa Details", color = Color.White) },
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
                .background(Color.White)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = RedPrimary
                )
            } else if (villa != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Gray)
                    ) {
                        if (villa!!.photos.isNotEmpty()) {
                            AsyncImage(
                                model = villa!!.photos.first(),
                                contentDescription = "Villa Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No Image", color = Color.White)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = villa!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = RedSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(villa!!.location, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAECEC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Price", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                                Text(
                                    "Rp ${villa!!.price.toInt()} / night",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = RedPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            villa!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Owner Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Name: ${villa!!.owner?.name ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Email: ${villa!!.owner?.email ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
                        Text("ID: ${villa!!.ownerId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                Text("Failed to load details", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
