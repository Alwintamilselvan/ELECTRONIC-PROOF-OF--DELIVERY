package com.ats.epodapp.jetpack_compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ats.epodapp.jetpack_compose.theme.AppColors

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    SUCCESS,
    WARNING,
    DANGER
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val gradient = when (variant) {
        ButtonVariant.PRIMARY -> Brush.horizontalGradient(
            colors = listOf(AppColors.GradientBlueStart, AppColors.GradientBlueEnd)
        )
        ButtonVariant.SECONDARY -> Brush.horizontalGradient(
            colors = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        )
        ButtonVariant.SUCCESS -> Brush.horizontalGradient(
            colors = listOf(AppColors.GradientGreenStart, AppColors.GradientGreenEnd)
        )
        ButtonVariant.WARNING -> Brush.horizontalGradient(
            colors = listOf(AppColors.StatusPending, AppColors.StatusPendingLight)
        )
        ButtonVariant.DANGER -> Brush.horizontalGradient(
            colors = listOf(AppColors.StatusFailed, AppColors.StatusFailedLight)
        )
    }

    val textColor = when (variant) {
        ButtonVariant.SECONDARY -> AppColors.TextPrimary
        else -> Color.White
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) gradient else Brush.horizontalGradient(
                colors = listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f))
            ))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = textColor.copy(alpha = if (enabled) 1f else 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = if (enabled) 1f else 0.5f)
            )
        }
    }
}
