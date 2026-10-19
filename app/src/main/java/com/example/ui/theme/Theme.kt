package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerGreen,
    onPrimaryContainer = OnPrimaryContainerGreen,
    secondary = WarmAmber,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainerAmber,
    onSecondaryContainer = OnSecondaryContainerAmber,
    tertiary = TrustBlue,
    onTertiary = Color.White,
    tertiaryContainer = TrustBlueContainer,
    background = BackgroundLight,
    onBackground = Color(0xFF191C19),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C19),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF414941),
    outline = OutlineLight,
    outlineVariant = OutlineSubtle,
    error = ErrorRed,
    errorContainer = ErrorContainerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenDarkMode,
    onPrimary = Color(0xFF00391F),
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryContainerGreen,
    secondary = WarmAmberDarkMode,
    onSecondary = Color(0xFF472200),
    secondaryContainer = WarmAmberDark,
    onSecondaryContainer = SecondaryContainerAmber,
    background = BackgroundDark,
    onBackground = Color(0xFFE1E4DF),
    surface = SurfaceDark,
    onSurface = Color(0xFFE1E4DF),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC1C9BF),
    outline = Color(0xFF8B938A),
    error = Color(0xFFFFB4AB)
)

@Composable
fun BorrowBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand identity prominent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
