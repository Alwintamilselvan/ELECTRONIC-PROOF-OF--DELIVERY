package com.ats.epodapp.jetpack_compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ats.epodapp.jetpack_compose.models.DeliveryStatus

@Composable
fun StatusBadge(
    status: DeliveryStatus,
    modifier: Modifier = Modifier
) {
    val (gradient, borderColor, textColor, text) = when (status) {
        DeliveryStatus.DELIVERED -> {
            Tuple4(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))
                ),
                Color(0xFFA7F3D0),
                Color(0xFF065F46),
                "Delivered"
            )
        }
        DeliveryStatus.PENDING -> {
            Tuple4(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                ),
                Color(0xFFFDE68A),
                Color(0xFF92400E),
                "Pending"
            )
        }
        DeliveryStatus.OUT_FOR_DELIVERY -> {
            Tuple4(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE))
                ),
                Color(0xFFBFDBFE),
                Color(0xFF1E40AF),
                "Out for Delivery"
            )
        }
        DeliveryStatus.FAILED -> {
            Tuple4(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFEE2E2), Color(0xFFFECACA))
                ),
                Color(0xFFFECACA),
                Color(0xFF991B1B),
                "Failed"
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
