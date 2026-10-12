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
    // 01 & 02. Principais e Composição (Spacings)
    val minimal: Dp = 2.dp,
    val xxSmall: Dp = 4.dp,
    val xSmall: Dp = 8.dp,
    val small: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 32.dp,
    val xxLarge: Dp = 40.dp,

    // 03. Ícones
    val iconXSmall: Dp = 12.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 20.dp,
    val iconLarge: Dp = 24.dp,
    val iconXLarge: Dp = 36.dp,
    val iconHuge: Dp = 40.dp,

    // 04. Radius and Borders
    val borderMinimal: Dp = 1.dp,
    val borderMax: Dp = 3.dp,
    val radiusMinimal: Dp = 4.dp,
    val radiusXSmall: Dp = 8.dp,
    val radiusSmall: Dp = 12.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusFull: Dp = 100.dp,

    // Backwards Compatibility Aliases
    @Deprecated("Use minimal instead", ReplaceWith("minimal"))
    val xxxSmall: Dp = 2.dp,
    @Deprecated("Use radiusLarge instead", ReplaceWith("radiusLarge"))
    val cornerRadius: Dp = 24.dp,
)

val LocalAureoleDimens = staticCompositionLocalOf { AureoleDimens() }

@Deprecated("Use AureoleDimens instead", ReplaceWith("AureoleDimens"))
typealias AureoleSpacing = AureoleDimens
