package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design system dimension tokens for Aureole Launcher based on Figma rules.
 * Follows a standard 4dp/8dp grid system for consistent margins, padding, and layout gaps.
 */
@Immutable
data class AureoleDimens(
    val none: Dp = 0.dp,
    val xxxSmall: Dp = 2.dp,
    val xxSmall: Dp = 4.dp,
    val xSmall: Dp = 8.dp,
    val small: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 20.dp,
    val xLarge: Dp = 24.dp,
    val xxLarge: Dp = 32.dp,
    val xxxLarge: Dp = 48.dp,
    val huge: Dp = 64.dp,
    val cornerRadius: Dp = 20.dp,
)

val LocalAureoleDimens = staticCompositionLocalOf { AureoleDimens() }

@Deprecated("Use AureoleDimens instead", ReplaceWith("AureoleDimens"))
typealias AureoleSpacing = AureoleDimens
