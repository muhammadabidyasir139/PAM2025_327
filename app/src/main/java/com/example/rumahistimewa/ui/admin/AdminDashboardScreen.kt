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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rumahistimewa.ui.theme.RedPrimary

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: AdminDashboardViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    AdminLayout(
        title = "Admin Dashboard",
        onNavigate = onNavigate,
        onLogout = onLogout,
        onHomeClick = { viewModel.fetchDashboardData() }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else {
                Column(
                    modifier = Modifier
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.revenuePeriod == "day") "Revenue (Today)" else "Revenue (This Week)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuggestionChip(
                                onClick = { viewModel.fetchRevenueData("day") },
                                label = { Text("Day") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (state.revenuePeriod == "day") RedPrimary else Color.Transparent,
                                    labelColor = if (state.revenuePeriod == "day") Color.White else Color.Black
                                )
                            )
                            SuggestionChip(
                                onClick = { viewModel.fetchRevenueData("week") },
                                label = { Text("Week") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (state.revenuePeriod == "week") RedPrimary else Color.Transparent,
                                    labelColor = if (state.revenuePeriod == "week") Color.White else Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple Bar Chart
                    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
                    
                    if (state.chartData.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val count = state.chartData.size
                                    if (count > 0) {
                                        val gap = 20f
                                        val canvasWidth = size.width
                                        val barWidth = ((canvasWidth - (gap * (count + 1))) / count).coerceAtLeast(10f)
                                        val totalBarWidth = barWidth + gap
                                        
                                        // Calculate which bar was clicked
                                        val index = ((offset.x - gap) / totalBarWidth).toInt()
                                        if (index in 0 until count) {
                                            selectedBarIndex = if (selectedBarIndex == index) null else index
                                        } else {
                                            selectedBarIndex = null
                                        }
                                    }
                                }
                            }) {
                                val count = state.chartData.size
                                val gap = 20f
                                // Ensure barWidth is positive
                                val barWidth = ((size.width - (gap * (count + 1))) / count).coerceAtLeast(10f)
                                val maxHeight = size.height
                                
                                state.chartData.forEachIndexed { index, value ->
                                    val x = index * (barWidth + gap) + gap
                                    val barHeight = maxHeight * value
                                    val y = maxHeight - barHeight
                                    
                                    drawRect(
                                        color = if (selectedBarIndex == index) RedPrimary.copy(alpha = 0.7f) else RedPrimary,
                                        topLeft = Offset(x = x, y = y),
                                        size = Size(width = barWidth, height = barHeight)
                                    )
                                }
                            }
                            
                            // Tooltip Overlay
                            selectedBarIndex?.let { index ->
                                if (index >= 0 && index < state.chartLabels.size && index < state.chartRawData.size) {
                                    val rawValue = state.chartRawData[index]
                                    val label = state.chartLabels[index]
                                    
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp),
                                            shadowElevation = 4.dp
                                        ) {
                                            Text(
                                                text = "${label}: ${formatter.format(rawValue)}",
                                                modifier = Modifier.padding(8.dp),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Labels
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            state.chartLabels.take(7).forEach { label ->
                                Text(text = label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("No data available")
                        }
                    }
                }
            }

            // Summary Cards
            Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            val summaryData = listOf(
                SummaryItem("Total Users", state.totalUsers.toString(), Color(0xFFE3F2FD), Color(0xFF1565C0)),
                SummaryItem("Total Villas", state.totalVillas.toString(), Color(0xFFE8F5E9), Color(0xFF2E7D32)),
                SummaryItem("Transactions", state.totalTransactions.toString(), Color(0xFFFFF3E0), Color(0xFFEF6C00)),
                SummaryItem("Total Revenue", formatter.format(state.totalRevenue), Color(0xFFFCE4EC), RedPrimary)
            )

            // Changed to List (Column) instead of Grid/Row
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                summaryData.forEach { item ->
                    SummaryCard(item, Modifier.fillMaxWidth())
                }
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
