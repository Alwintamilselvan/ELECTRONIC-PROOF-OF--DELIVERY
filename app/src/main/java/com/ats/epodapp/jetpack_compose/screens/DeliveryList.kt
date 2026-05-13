package com.ats.epodapp.jetpack_compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ats.epodapp.jetpack_compose.components.*
import com.ats.epodapp.jetpack_compose.models.Delivery
import com.ats.epodapp.jetpack_compose.models.MockDeliveries
import com.ats.epodapp.jetpack_compose.theme.AppColors

@Composable
fun DeliveryListScreen(
    onBackClick: () -> Unit,
    onDeliveryClick: (String) -> Unit,
    onViewPodClick: () -> Unit
) {
    val deliveries = MockDeliveries.deliveries

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.BackgroundGradientStart,
                        AppColors.BackgroundGradientMid.copy(alpha = 0.3f),
                        AppColors.BackgroundGradientEnd.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            DriverTopAppBar(
                title = "My Deliveries",
                onBackClick = onBackClick
            )

            // Delivery List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(deliveries) { delivery ->
                    DeliveryCard(
                        delivery = delivery,
                        onClick = { onDeliveryClick(delivery.id) }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // POD View Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = onViewPodClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                ) {
                    Icon(imageVector = Icons.Default.Inventory, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View POD History", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DeliveryCard(
    delivery: Delivery,
    onClick: () -> Unit
) {
    GradientCard(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header - Customer name and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = delivery.customerName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = delivery.packageId,
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
                StatusBadge(status = delivery.status)
            }

            // Divider
            HorizontalDivider(
                color = AppColors.Border,
                thickness = 1.dp
            )

            // Address
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = delivery.address,
                        fontSize = 14.sp,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "${delivery.city}, ${delivery.zipCode}",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            // Contact Number
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = delivery.contactNumber,
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}
