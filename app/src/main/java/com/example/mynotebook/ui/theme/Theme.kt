package com.example.mynotebook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyNotebookTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme(
        primary = Color(0xFF90CAF9),
        background = Color(0xFF121212),
        onBackground = Color.White
    ) else lightColorScheme(
        primary = Color(0xFF1976D2),
        background = Color(0xFFF5F5F5),
        onBackground = Color.Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}