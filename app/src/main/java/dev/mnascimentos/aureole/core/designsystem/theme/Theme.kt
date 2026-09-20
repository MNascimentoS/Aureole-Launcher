package dev.mnascimentos.aureole.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils

private const val HSL_COMPONENTS_COUNT = 3

private const val DARK_PRIMARY_L = 0.80f
private const val DARK_ON_PRIMARY_L = 0.20f
private const val DARK_PRIMARY_CONTAINER_L = 0.30f
private const val DARK_ON_PRIMARY_CONTAINER_L = 0.90f

private const val LIGHT_PRIMARY_L = 0.40f
private const val LIGHT_PRIMARY_CONTAINER_L = 0.90f
private const val LIGHT_ON_PRIMARY_CONTAINER_L = 0.15f

private const val SEC_SAT_FACTOR = 0.4f
private const val TERT_HUE_SHIFT = 60f
private const val HUE_MAX = 360f
private const val BG_SAT_FACTOR = 0.1f

private const val DARK_BG_L = 0.08f
private const val DARK_ON_BG_L = 0.90f
private const val DARK_SURFACE_VARIANT_L = 0.18f
private const val DARK_ON_SURFACE_VARIANT_L = 0.80f
private const val DARK_OUTLINE_L = 0.60f
private const val DARK_OUTLINE_VARIANT_L = 0.30f

private const val LIGHT_BG_L = 0.98f
private const val LIGHT_ON_BG_L = 0.10f
private const val LIGHT_SURFACE_VARIANT_L = 0.92f
private const val LIGHT_ON_SURFACE_VARIANT_L = 0.30f
private const val LIGHT_OUTLINE_L = 0.50f
private const val LIGHT_OUTLINE_VARIANT_L = 0.80f

@Composable
fun AureoleLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicWallpaperEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    seedColor: Color = DefaultSeedColor,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        (isDynamicWallpaperEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            try {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } catch (_: Exception) {
                generateCustomColorScheme(seedColor = seedColor, isDark = darkTheme)
            }
        }
        else -> generateCustomColorScheme(seedColor = seedColor, isDark = darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

fun generateCustomColorScheme(seedColor: Color, isDark: Boolean): ColorScheme {
    val seedArgb = seedColor.toArgb()
    val hsl = FloatArray(HSL_COMPONENTS_COUNT)
    ColorUtils.colorToHSL(seedArgb, hsl)
    val hue = hsl[0]
    val saturation = hsl[1]

    return if (isDark) {
        generateDarkColorScheme(hue, saturation)
    } else {
        generateLightColorScheme(hue, saturation)
    }
}

private fun generateDarkColorScheme(hue: Float, saturation: Float): ColorScheme {
    val primary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, DARK_PRIMARY_L)))
    val onPrimary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, DARK_ON_PRIMARY_L)))
    val primaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, DARK_PRIMARY_CONTAINER_L)))
    val onPrimaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, DARK_ON_PRIMARY_CONTAINER_L)))

    val secSat = (saturation * SEC_SAT_FACTOR).coerceIn(0f, 1f)
    val secondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, DARK_PRIMARY_L)))
    val onSecondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, DARK_ON_PRIMARY_L)))
    val secondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, DARK_PRIMARY_CONTAINER_L)))
    val onSecondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, DARK_ON_PRIMARY_CONTAINER_L)))

    val tertHue = (hue + TERT_HUE_SHIFT) % HUE_MAX
    val tertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, DARK_PRIMARY_L)))
    val onTertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, DARK_ON_PRIMARY_L)))
    val tertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, DARK_PRIMARY_CONTAINER_L)))
    val onTertiaryContainer = Color(
        ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, DARK_ON_PRIMARY_CONTAINER_L))
    )

    val bgSat = (saturation * BG_SAT_FACTOR).coerceIn(0f, 1f)
    val background = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_BG_L)))
    val onBackground = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_ON_BG_L)))
    val surface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_BG_L)))
    val onSurface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_ON_BG_L)))
    val surfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_SURFACE_VARIANT_L)))
    val onSurfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_ON_SURFACE_VARIANT_L)))
    val outline = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_OUTLINE_L)))
    val outlineVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, DARK_OUTLINE_VARIANT_L)))

    return darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
    )
}

private fun generateLightColorScheme(hue: Float, saturation: Float): ColorScheme {
    val primary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, LIGHT_PRIMARY_L)))
    val onPrimary = Color.White
    val primaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, LIGHT_PRIMARY_CONTAINER_L)))
    val onPrimaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, LIGHT_ON_PRIMARY_CONTAINER_L)))

    val secSat = (saturation * SEC_SAT_FACTOR).coerceIn(0f, 1f)
    val secondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, LIGHT_PRIMARY_L)))
    val onSecondary = Color.White
    val secondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, LIGHT_PRIMARY_CONTAINER_L)))
    val onSecondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, LIGHT_ON_PRIMARY_CONTAINER_L)))

    val tertHue = (hue + TERT_HUE_SHIFT) % HUE_MAX
    val tertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, LIGHT_PRIMARY_L)))
    val onTertiary = Color.White
    val tertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, LIGHT_PRIMARY_CONTAINER_L)))
    val onTertiaryContainer = Color(
        ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, LIGHT_ON_PRIMARY_CONTAINER_L))
    )

    val bgSat = (saturation * BG_SAT_FACTOR).coerceIn(0f, 1f)
    val background = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_BG_L)))
    val onBackground = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_ON_BG_L)))
    val surface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_BG_L)))
    val onSurface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_ON_BG_L)))
    val surfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_SURFACE_VARIANT_L)))
    val onSurfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_ON_SURFACE_VARIANT_L)))
    val outline = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_OUTLINE_L)))
    val outlineVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, LIGHT_OUTLINE_VARIANT_L)))

    return lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
    )
}
