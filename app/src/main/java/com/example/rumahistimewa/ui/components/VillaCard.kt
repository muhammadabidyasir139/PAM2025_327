package com.example.rumahistimewa.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumahistimewa.ui.theme.RedSecondary
import com.example.rumahistimewa.ui.theme.White

@Composable
fun VillaCard(
    title: String,
    location: String,
    price: String,
    rating: Double,
    imageUrl: String?, // Placeholder for now
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Image
            if (imageUrl != null) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop,
                    error = painterResource(android.R.drawable.ic_menu_report_image), // Fallback
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )
            } else {
                 Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray)
                ) {
                     // Placeholder
                     Icon(
                         imageVector = Icons.Default.Home,
                         contentDescription = null,
                         tint = Color.White,
                         modifier = Modifier.align(Alignment.Center).size(48.dp)
                     )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleSmall,
                    color = RedSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun VillaCardPreview() {
    VillaCard(
        title = "Executive Suite Villa",
        location = "Bali, Indonesia",
        price = "Rp 2.500.000 / night",
        rating = 4.8,
        imageUrl = null,
        onClick = {}
    )
}
