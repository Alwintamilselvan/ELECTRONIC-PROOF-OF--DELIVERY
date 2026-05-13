package com.ats.epodapp.jetpack_compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ats.epodapp.jetpack_compose.theme.AppColors

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradient: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp)
        )
        .clip(RoundedCornerShape(16.dp))
        .then(
            if (gradient != null) {
                Modifier.background(gradient)
            } else {
                Modifier
                    .background(Color.White)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(16.dp))
            }
        )
        .then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }
        )

    Box(
        modifier = cardModifier,
        content = content
    )
}
