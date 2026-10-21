package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design system dimension tokens for Aureole Launcher based on Figma rules.
 */
@Immutable
data class AureoleDimens(
    // 01. Principais e Composição (Spacings & Radii)
    val zero: Dp = 0.dp,
    val minimal: Dp = 2.dp,
    val xxSmall: Dp = 4.dp,
    val xSmall: Dp = 8.dp,
    val smallCompact: Dp = 10.dp,
    val small: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 32.dp,
    val xxLarge: Dp = 40.dp,
    val minTouchTarget: Dp = 48.dp,

    // 02. Ícones
    val iconXSmall: Dp = 12.dp,
    val iconSmallCompact: Dp = 14.dp,
    val iconSmall: Dp = 16.dp,
    val iconMediumSmall: Dp = 18.dp,
    val iconMedium: Dp = 20.dp,
    val iconLargePlus: Dp = 22.dp,
    val iconLarge: Dp = 24.dp,
    val iconXLargeSmall: Dp = 26.dp,
    val iconXLargeMedium: Dp = 28.dp,
    val iconXLarge: Dp = 36.dp,
    val iconHuge: Dp = 40.dp,

    // 03. Radius and Borders
    val borderMinimal: Dp = 1.dp,
    val borderMax: Dp = 3.dp,
    val radiusMinimal: Dp = 4.dp,
    val radiusXSmall: Dp = 8.dp,
    val radiusSmallCompact: Dp = 10.dp,
    val radiusSmall: Dp = 12.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusMediumLarge: Dp = 20.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusFull: Dp = 100.dp,
)

val LocalAureoleDimens = staticCompositionLocalOf { AureoleDimens() }
