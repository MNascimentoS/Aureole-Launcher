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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIcons
import dev.mnascimentos.aureole.core.designsystem.icons.LocalAureoleIcons

data class AureoleThemeConfig(
    val darkTheme: Boolean,
    val isDynamicWallpaperEnabled: Boolean = false,
    val aureoleColors: AureoleColors = frostbiteColors,
    val aureoleFontName: String = "Istok Web",
    val aureoleIcons: AureoleIcons = AureoleIcons(),
    val aureoleDimens: AureoleDimens = AureoleDimens()
)

@Composable
fun AureoleLauncherTheme(
    config: AureoleThemeConfig = AureoleThemeConfig(darkTheme = isSystemInDarkTheme()),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        config.isDynamicWallpaperEnabled && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (config.darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        config.darkTheme -> buildDarkColorScheme(config.aureoleColors)
        else -> buildLightColorScheme(config.aureoleColors)
    }

    val aureoleTypography = AureoleTypography(fontFamily = AvailableFonts[config.aureoleFontName] ?: IstokWebFontFamily)

    CompositionLocalProvider(
        LocalAureoleColors provides config.aureoleColors,
        LocalAureoleIcons provides config.aureoleIcons,
        LocalAureoleTypography provides aureoleTypography,
        LocalAureoleDimens provides config.aureoleDimens,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = aureoleTypography.toMaterialTypography(),
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
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleColors.current

    val icons: AureoleIcons
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleIcons.current

    val typography: AureoleTypography
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleTypography.current

    val dimens: AureoleDimens
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleDimens.current
}

// Kept for backwards compatibility with existing screens
object AureoleTheme {
    val colors: AureoleColors
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleColors.current

    val typography: AureoleTypography
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleTypography.current

    val dimens: AureoleDimens
        @ReadOnlyComposable
        @Composable
        get() = LocalAureoleDimens.current
}
