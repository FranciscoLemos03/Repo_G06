package com.example.share2care.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(52, 51, 74), // Primary Buttons Color
    onPrimary = Color(245, 244, 255), // Text Color on Primary Buttons
    background = Color(31, 30, 44), // Dark blue-gray background
    onBackground = Color(245, 244, 255), // Text color on the background
    secondary = Color(204, 92, 255), // Secondary Color (Pink)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme, // Use custom dark theme colors
        typography = Typography,
        content = content
    )
}
