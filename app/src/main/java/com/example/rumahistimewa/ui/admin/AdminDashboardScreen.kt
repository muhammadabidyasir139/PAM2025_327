package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    AdminLayout(
        title = "Admin Dashboard",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Revenue Chart Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().height(250.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Revenue (Last 7 Days)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Simple Bar Chart
                    Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        val barWidth = size.width / 9
                        val maxHeight = size.height
                        val data = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, 0.7f) // Normalized
                        
                        data.forEachIndexed { index, value ->
                            drawRect(
                                color = RedPrimary,
                                topLeft = Offset(x = index * (barWidth + 20f) + 20f, y = maxHeight - (maxHeight * value)),
                                size = Size(width = barWidth, height = maxHeight * value)
                            )
                        }
                    }
                }
            }

            // Summary Cards
            Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            val summaryData = listOf(
                SummaryItem("Total Users", "1,250", Color(0xFFE3F2FD), Color(0xFF1565C0)),
                SummaryItem("Active Villas", "45", Color(0xFFE8F5E9), Color(0xFF2E7D32)),
                SummaryItem("Transactions", "320", Color(0xFFFFF3E0), Color(0xFFEF6C00)),
                SummaryItem("Total Revenue", "Rp 24.5M", Color(0xFFFCE4EC), RedPrimary)
            )

            // Using a simple Column for rows instead of LazyGrid inside ScrollView to avoid nested scrolling issues
            summaryData.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { item ->
                        SummaryCard(item, Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

data class SummaryItem(val title: String, val value: String, val bgColor: Color, val textColor: Color)

@Composable
fun SummaryCard(item: SummaryItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = item.bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(item.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = item.textColor)
        }
    }
}
