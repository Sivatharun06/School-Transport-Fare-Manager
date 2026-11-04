package com.schooltransport.faremanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF673AB7)
private val DeepPurple = Color(0xFF452080)
private val Gold = Color(0xFFFFC107)
private val LightBackground = Color(0xFFF8F5FF)
private val DarkBackground = Color(0xFF17121F)

private val LightColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Color(0xFF302000),
    background = LightBackground,
    onBackground = Color(0xFF211B2B),
    surface = Color.White,
    onSurface = Color(0xFF211B2B),
    error = Color(0xFFBA1A1A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB69CFF),
    onPrimary = Color(0xFF30116B),
    secondary = Gold,
    onSecondary = Color(0xFF302000),
    background = DarkBackground,
    onBackground = Color(0xFFF0E8FF),
    surface = Color(0xFF241D30),
    onSurface = Color(0xFFF0E8FF),
    error = Color(0xFFFFB4AB)
)

@Composable
fun SchoolTransportTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
