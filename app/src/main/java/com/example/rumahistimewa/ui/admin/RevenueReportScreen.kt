package com.example.rumahistimewa.ui.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun RevenueReportScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    AdminLayout(
        title = "Revenue Report",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Mode Toggle
            var isMonthly by remember { mutableStateOf(true) }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { isMonthly = false },
                    colors = ButtonDefaults.buttonColors(containerColor = if (!isMonthly) RedPrimary else Color.Gray)
                ) {
                    Text("Daily")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { isMonthly = true },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isMonthly) RedPrimary else Color.Gray)
                ) {
                    Text("Monthly")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RedPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (isMonthly) "Total Revenue (This Month)" else "Total Revenue (Today)", color = Color.White)
                    Text(if (isMonthly) "$12,500" else "$420", style = MaterialTheme.typography.displayMedium, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Trends", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Charts
            if (isMonthly) {
               BarChart()
            } else {
               LineChart()
            }
        }
    }
}

@Composable
fun BarChart() {
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val barWidth = size.width / 7
        val maxHeight = size.height
        val data = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f) // Normalized data
        
        data.forEachIndexed { index, value ->
            drawRect(
                color = RedPrimary,
                topLeft = Offset(x = index * (barWidth + 10f), y = maxHeight - (maxHeight * value)),
                size = Size(width = barWidth, height = maxHeight * value)
            )
        }
        
        drawLine(
            color = Color.Black,
            start = Offset(0f, maxHeight),
            end = Offset(size.width, maxHeight),
            strokeWidth = 4f
        )
    }
}

@Composable
fun LineChart() {
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val width = size.width
        val height = size.height
        val data = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, 0.7f)
        val stepX = width / (data.size - 1)
        
        val path = Path().apply {
            moveTo(0f, height - (data[0] * height))
            for (i in 1 until data.size) {
                lineTo(i * stepX, height - (data[i] * height))
            }
        }
        
        drawPath(
            path = path,
            color = RedPrimary,
            style = Stroke(width = 5f)
        )
        
        drawLine(
            color = Color.Black,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 4f
        )
    }
}
