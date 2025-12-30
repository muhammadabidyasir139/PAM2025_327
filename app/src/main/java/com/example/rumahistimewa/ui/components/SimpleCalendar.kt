package com.example.rumahistimewa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.example.rumahistimewa.ui.theme.RedPrimary

@Composable
fun SimpleCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
            }
            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of Week
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            daysOfWeek.forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = day, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid
        val firstDayOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()
        val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value - 1 // 0 = Mon, 6 = Sun

        Column {
            var currentDay = 1
            // 6 rows max
            for (row in 0 until 6) {
                if (currentDay > daysInMonth) break
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        if (row == 0 && col < dayOfWeekOffset) {
                            // Empty slot
                            Box(modifier = Modifier.weight(1f))
                        } else if (currentDay <= daysInMonth) {
                            val date = yearMonth.atDay(currentDay)
                            val isSelected = selectedDate == date
                            // Prevent selecting past dates
                            val isPast = date.isBefore(LocalDate.now())
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .background(
                                        when {
                                            isSelected -> RedPrimary
                                            !isPast -> RedPrimary.copy(alpha = 0.2f) 
                                            else -> Color.Transparent
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable(enabled = !isPast) { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentDay.toString(),
                                    color = if (isSelected) Color.White else if (!isPast) RedPrimary else Color.Gray,
                                    fontWeight = if (isSelected || !isPast) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            currentDay++
                        } else {
                            // Empty slot after month end
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
