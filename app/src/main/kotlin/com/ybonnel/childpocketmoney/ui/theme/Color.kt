package com.ybonnel.childpocketmoney.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// Primary palette - green tones for money
val Green80 = Color(0xFF4CAF50)
val Green40 = Color(0xFF2E7D32)
val GreenContainer80 = Color(0xFFA5D6A7)
val GreenContainer40 = Color(0xFF1B5E20)

// Secondary - amber
val Amber80 = Color(0xFFFFC107)
val Amber40 = Color(0xFFF57F17)

// Child avatar colors - predefined set
val AvatarColors = listOf(
    Color(0xFF4CAF50), // Green
    Color(0xFF2196F3), // Blue
    Color(0xFFF44336), // Red
    Color(0xFF9C27B0), // Purple
    Color(0xFFFF9800), // Orange
    Color(0xFF00BCD4), // Cyan
    Color(0xFFE91E63), // Pink
    Color(0xFF795548), // Brown
    Color(0xFF607D8B), // Blue Grey
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF3F51B5), // Indigo
    Color(0xFF009688), // Teal
)

// Default child color
val DefaultChildColor = AvatarColors.first().toArgb()
