package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LicenseXColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = VoidBackground,
    primaryContainer = PanelSurfaceHigh,
    onPrimaryContainer = TextPrimary,
    secondary = CyberViolet,
    onSecondary = TextPrimary,
    secondaryContainer = PanelSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = CyberPink,
    onTertiary = TextPrimary,
    background = VoidBackground,
    onBackground = TextPrimary,
    surface = PanelSurface,
    onSurface = TextPrimary,
    surfaceVariant = PanelSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    error = CyberDanger,
    onError = TextPrimary,
    outline = HudBorder,
    outlineVariant = HudBorderBright
)

private val LicenseXShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(14.dp)
)

@Composable
fun LicenseXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LicenseXColorScheme,
        typography = LicenseXTypography,
        shapes = LicenseXShapes,
        content = content
    )
}
