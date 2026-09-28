package dev.mnascimentos.aureole.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIcons
import dev.mnascimentos.aureole.core.designsystem.icons.LocalAureoleIcons

@Composable
fun AureoleLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicWallpaperEnabled: Boolean = false,
    aureoleColors: AureoleColors = frostbiteColors,
    aureoleIcons: AureoleIcons = AureoleIcons(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        isDynamicWallpaperEnabled && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> buildDarkColorScheme(aureoleColors)
        else -> buildLightColorScheme(aureoleColors)
    }

    CompositionLocalProvider(
        LocalAureoleColors provides aureoleColors,
        LocalAureoleIcons provides aureoleIcons,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AureoleTypography,
            content = content,
        )
    }
}

private fun buildDarkColorScheme(aureoleColors: AureoleColors) = darkColorScheme(
    background = aureoleColors.background,
    onBackground = aureoleColors.onSurfaceHigh,
    surface = aureoleColors.surface,
    onSurface = aureoleColors.onSurfaceHigh,
    surfaceVariant = aureoleColors.surfaceVariant,
    onSurfaceVariant = aureoleColors.onSurfaceMedium,
    outline = aureoleColors.outline,
    primary = aureoleColors.onSurfaceHigh,
    onPrimary = aureoleColors.surface,
    primaryContainer = aureoleColors.surfaceVariant,
    onPrimaryContainer = aureoleColors.onSurfaceHigh,
    surfaceContainer = aureoleColors.surface,
    surfaceContainerHigh = aureoleColors.surfaceVariant,
    surfaceContainerHighest = aureoleColors.surfaceVariant,
    surfaceContainerLow = aureoleColors.surface,
    surfaceContainerLowest = aureoleColors.background,
    secondary = aureoleColors.onSurfaceMedium,
    onSecondary = aureoleColors.surface,
    secondaryContainer = aureoleColors.surfaceVariant,
    onSecondaryContainer = aureoleColors.onSurfaceMedium,
)

private fun buildLightColorScheme(aureoleColors: AureoleColors) = lightColorScheme(
    background = aureoleColors.background,
    onBackground = aureoleColors.onSurfaceHigh,
    surface = aureoleColors.surface,
    onSurface = aureoleColors.onSurfaceHigh,
    surfaceVariant = aureoleColors.surfaceVariant,
    onSurfaceVariant = aureoleColors.onSurfaceMedium,
    outline = aureoleColors.outline,
    primary = aureoleColors.onSurfaceHigh,
    onPrimary = aureoleColors.surface,
    primaryContainer = aureoleColors.surfaceVariant,
    onPrimaryContainer = aureoleColors.onSurfaceHigh,
    surfaceContainer = aureoleColors.surface,
    surfaceContainerHigh = aureoleColors.surfaceVariant,
    surfaceContainerHighest = aureoleColors.surfaceVariant,
    surfaceContainerLow = aureoleColors.surface,
    surfaceContainerLowest = aureoleColors.background,
    secondary = aureoleColors.onSurfaceMedium,
    onSecondary = aureoleColors.surface,
    secondaryContainer = aureoleColors.surfaceVariant,
    onSecondaryContainer = aureoleColors.onSurfaceMedium,
)

// Global Design System Accessor
object AureoleDS {
    val colors: AureoleColors
        @Composable
        get() = LocalAureoleColors.current

    val icons: AureoleIcons
        @Composable
        get() = LocalAureoleIcons.current

    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography
}

// Kept for backwards compatibility with existing screens
object AureoleTheme {
    val colors: AureoleColors
        @Composable
        get() = LocalAureoleColors.current

    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography
}
