package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RevenueReportScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: AdminDashboardViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    // Initial fetch
    LaunchedEffect(Unit) {
        viewModel.fetchRevenueData("week")
    }

    AdminLayout(
        title = "Revenue Report",
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
            // Summary Cards for the selected period (List Vertical)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transactions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Transactions", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Text(
                            state.periodTransactions.toString(), 
                            style = MaterialTheme.typography.headlineSmall, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFFEF6C00)
                        )
                    }
                }

                // Revenue Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Revenue", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Text(
                            formatter.format(state.periodRevenue), 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Bold, 
                            color = RedPrimary
                        )
                    }
                }
            }

            // Interactive Chart Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header & Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Revenue Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("day", "week", "month").forEach { period ->
                                FilterChip(
                                    selected = state.revenuePeriod == period,
                                    onClick = { viewModel.fetchRevenueData(period) },
                                    label = { 
                                        Text(
                                            period.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                            maxLines = 1
                                        ) 
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RedPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.Transparent,
                                        labelColor = Color.Black
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = state.revenuePeriod == period,
                                        borderColor = if (state.revenuePeriod == period) RedPrimary else Color.Gray,
                                        borderWidth = 1.dp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Interactive Bar Chart
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
                            // Show a subset of labels if too many
                            val labelsToShow = if (state.chartLabels.size > 7) {
                                val step = state.chartLabels.size / 6
                                state.chartLabels.filterIndexed { index, _ -> index % step == 0 }.take(7)
                            } else {
                                state.chartLabels
                            }
                            
                            labelsToShow.forEach { label ->
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
        }
    }
}
