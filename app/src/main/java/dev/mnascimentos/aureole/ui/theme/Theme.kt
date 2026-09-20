@file:Suppress("FunctionNaming", "MagicNumber", "LongMethod")

package dev.mnascimentos.aureole.ui.theme

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
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seedArgb, hsl)
    val hue = hsl[0]
    val saturation = hsl[1]

    return if (isDark) {
        val primary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.80f)))
        val onPrimary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.20f)))
        val primaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.30f)))
        val onPrimaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.90f)))

        val secSat = (saturation * 0.4f).coerceIn(0f, 1f)
        val secondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.80f)))
        val onSecondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.20f)))
        val secondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.30f)))
        val onSecondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.90f)))

        val tertHue = (hue + 60f) % 360f
        val tertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.80f)))
        val onTertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.20f)))
        val tertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.30f)))
        val onTertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.90f)))

        val bgSat = (saturation * 0.1f).coerceIn(0f, 1f)
        val background = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.08f)))
        val onBackground = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.90f)))
        val surface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.08f)))
        val onSurface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.90f)))
        val surfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.18f)))
        val onSurfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.80f)))
        val outline = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.60f)))
        val outlineVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.30f)))

        darkColorScheme(
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
    } else {
        val primary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.40f)))
        val onPrimary = Color.White
        val primaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.90f)))
        val onPrimaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, 0.15f)))

        val secSat = (saturation * 0.4f).coerceIn(0f, 1f)
        val secondary = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.40f)))
        val onSecondary = Color.White
        val secondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.90f)))
        val onSecondaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(hue, secSat, 0.15f)))

        val tertHue = (hue + 60f) % 360f
        val tertiary = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.40f)))
        val onTertiary = Color.White
        val tertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.90f)))
        val onTertiaryContainer = Color(ColorUtils.HSLToColor(floatArrayOf(tertHue, saturation, 0.15f)))

        val bgSat = (saturation * 0.1f).coerceIn(0f, 1f)
        val background = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.98f)))
        val onBackground = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.10f)))
        val surface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.98f)))
        val onSurface = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.10f)))
        val surfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.92f)))
        val onSurfaceVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.30f)))
        val outline = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.50f)))
        val outlineVariant = Color(ColorUtils.HSLToColor(floatArrayOf(hue, bgSat, 0.80f)))

        lightColorScheme(
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
}
