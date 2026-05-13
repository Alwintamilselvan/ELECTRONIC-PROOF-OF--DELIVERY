package com.ats.epodapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ats.epodapp.ui.theme.*

data class Delivery(
    val id: String,
    val customerName: String,
    val packageId: String,
    val address: String,
    val cityZip: String,
    val phone: String,
    val status: DeliveryStatus
)

enum class DeliveryStatus(val label: String, val color: Color, val bgColor: Color) {
    PENDING("Pending", AppOrange, AppLightOrange),
    OUT_FOR_DELIVERY("Out for Delivery", AppBlue, AppLightBlue),
    DELIVERED("Delivered", AppGreen, AppLightGreen),
    FAILED("Failed", AppRed, AppLightRed)
}

val mockDeliveries = listOf(
    Delivery("1", "Sarah Johnson", "PKG-2024-001", "123 Oak Street", "San Francisco, 94102", "+1 (555) 123-4567", DeliveryStatus.PENDING),
    Delivery("2", "Michael Chen", "PKG-2024-002", "456 Pine Avenue", "San Francisco, 94103", "+1 (555) 234-5678", DeliveryStatus.OUT_FOR_DELIVERY),
    Delivery("3", "Emily Rodriguez", "PKG-2024-003", "789 Maple Drive", "San Francisco, 94104", "+1 (555) 345-6789", DeliveryStatus.DELIVERED),
    Delivery("4", "David Kim", "PKG-2024-004", "321 Elm Boulevard", "San Francisco, 94105", "+1 (555) 456-7890", DeliveryStatus.FAILED)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Deliveries", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBlue)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockDeliveries) { delivery ->
                DeliveryCard(delivery)
            }
        }
    }
}

@Composable
fun DeliveryCard(delivery: Delivery) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(delivery.customerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTextDark)
                    Text(delivery.packageId, fontSize = 14.sp, color = AppTextGrey)
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = delivery.status.bgColor
                ) {
                    Text(
                        text = delivery.status.label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = delivery.status.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = AppTextGrey, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(delivery.address, fontSize = 14.sp, color = AppTextDark)
                    Text(delivery.cityZip, fontSize = 14.sp, color = AppTextGrey)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, null, tint = AppTextGrey, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(delivery.phone, fontSize = 14.sp, color = AppTextDark)
            }
        }
    }
}
