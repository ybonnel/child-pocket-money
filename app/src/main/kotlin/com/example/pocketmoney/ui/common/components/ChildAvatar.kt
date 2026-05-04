package com.example.pocketmoney.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChildAvatar(
    name: String,
    colorArgb: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val bgColor = Color(colorArgb)
    // Use white or black text based on relative luminance (WCAG formula).
    // Parentheses are critical: `and 0xFF` must be evaluated before `/ 255.0`.
    val r = (colorArgb shr 16 and 0xFF) / 255.0
    val g = (colorArgb shr 8 and 0xFF) / 255.0
    val b = (colorArgb and 0xFF) / 255.0
    val luminance = 0.299 * r + 0.587 * g + 0.114 * b
    val textColor = if (luminance > 0.5) Color.Black else Color.White

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = textColor,
            fontSize = (size.value * 0.45f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
