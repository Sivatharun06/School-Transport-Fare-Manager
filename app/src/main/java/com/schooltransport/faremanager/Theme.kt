
package com.schooltransport.faremanager.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// School Transport Fare Manager - Purple + Gold Light Theme

private val PrimaryPurple = Color(0xFF702FC5)
private val DeepPurple = Color(0xFF4F2397)
private val SoftPurple = Color(0xFFF5EEFC)
private val Gold = Color(0xFFFFDA45)
private val DarkGold = Color(0xFF8A6200)

private val AppBackground = Color(0xFFFAF7FE)
private val CardWhite = Color(0xFFFFFFFF)
private val MainText = Color(0xFF252033)
private val SecondaryText = Color(0xFF777184)

private val AppColors = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,

    primaryContainer = SoftPurple,
    onPrimaryContainer = DeepPurple,

    secondary = Gold,
    onSecondary = Color(0xFF302000),

    secondaryContainer = Color(0xFFFFF5CC),
    onSecondaryContainer = DarkGold,

    tertiary = Color(0xFF3B805A),
    onTertiary = Color.White,

    background = AppBackground,
    onBackground = MainText,

    surface = CardWhite,
    onSurface = MainText,

    surfaceVariant = Color(0xFFF5F0FA),
    onSurfaceVariant = SecondaryText,

    outline = Color(0xFFE2DCEA),
    outlineVariant = Color(0xFFECE7F2),

    error = Color(0xFFB3261E),
    onError = Color.White
)

private val AppTypography = Typography()

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SchoolTransportTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Initial release uses Light Mode only.
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
