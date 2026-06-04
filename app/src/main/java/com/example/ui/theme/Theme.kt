package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NaturalGreenPrimaryDark,
    onPrimary = NaturalGreenOnPrimaryDark,
    primaryContainer = NaturalGreenPrimaryContainerDark,
    onPrimaryContainer = NaturalGreenOnPrimaryContainerDark,
    secondary = NaturalGreenSecondaryDark,
    onSecondary = NaturalGreenOnSecondaryDark,
    secondaryContainer = NaturalGreenSecondaryContainerDark,
    onSecondaryContainer = NaturalGreenOnSecondaryContainerDark,
    tertiary = NaturalGreenTertiaryDark,
    onTertiary = NaturalGreenOnTertiaryDark,
    tertiaryContainer = NaturalGreenTertiaryContainerDark,
    onTertiaryContainer = NaturalGreenOnTertiaryContainerDark,
    background = NaturalBackgroundDark,
    onBackground = NaturalOnBackgroundDark,
    surface = NaturalSurfaceDark,
    onSurface = NaturalOnSurfaceDark,
    surfaceVariant = NaturalSurfaceVariantDark,
    onSurfaceVariant = NaturalOnSurfaceVariantDark,
    outline = NaturalOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalGreenPrimary,
    onPrimary = NaturalGreenOnPrimary,
    primaryContainer = NaturalGreenPrimaryContainer,
    onPrimaryContainer = NaturalGreenOnPrimaryContainer,
    secondary = NaturalGreenSecondary,
    onSecondary = NaturalGreenOnSecondary,
    secondaryContainer = NaturalGreenSecondaryContainer,
    onSecondaryContainer = NaturalGreenOnSecondaryContainer,
    tertiary = NaturalGreenTertiary,
    onTertiary = NaturalGreenOnTertiary,
    tertiaryContainer = NaturalGreenTertiaryContainer,
    onTertiaryContainer = NaturalGreenOnTertiaryContainer,
    background = NaturalBackgroundLight,
    onBackground = NaturalOnBackgroundLight,
    surface = NaturalSurfaceLight,
    onSurface = NaturalOnSurfaceLight,
    surfaceVariant = NaturalSurfaceVariantLight,
    onSurfaceVariant = NaturalOnSurfaceVariantLight,
    outline = NaturalOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamic flavor configuration to default-false to ensure the Natural Tones theme reigns supreme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
