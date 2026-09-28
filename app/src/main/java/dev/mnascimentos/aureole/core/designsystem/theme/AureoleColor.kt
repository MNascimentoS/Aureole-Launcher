@file:Suppress("MatchingDeclarationName")

package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AureoleColors(
    val onSurfaceHigh: Color,
    val onSurfaceMedium: Color,
    val onSurfaceLow: Color,
    val outline: Color,
    val surfaceVariant: Color,
    val surface: Color,
    val background: Color
)

val LocalAureoleColors = staticCompositionLocalOf {
    AureoleColors(
        onSurfaceHigh = Color.Unspecified,
        onSurfaceMedium = Color.Unspecified,
        onSurfaceLow = Color.Unspecified,
        outline = Color.Unspecified,
        surfaceVariant = Color.Unspecified,
        surface = Color.Unspecified,
        background = Color.Unspecified
    )
}

// Frostbite Theme Colors
private const val FROSTBITE_HIGH_HEX = 0xFFFFFFFF
private const val FROSTBITE_MEDIUM_HEX = 0xFFC5D8E8
private const val FROSTBITE_LOW_HEX = 0xFF9BB4C8
private const val FROSTBITE_OUTLINE_HEX = 0xFF7D96AA
private const val FROSTBITE_VARIANT_HEX = 0xFF4A5D6B
private const val FROSTBITE_SURFACE_HEX = 0xFF2B373E
private const val FROSTBITE_BG_HEX = 0xFF12181D

val FrostbiteOnSurfaceHigh = Color(FROSTBITE_HIGH_HEX)
val FrostbiteOnSurfaceMedium = Color(FROSTBITE_MEDIUM_HEX)
val FrostbiteOnSurfaceLow = Color(FROSTBITE_LOW_HEX)
val FrostbiteOutline = Color(FROSTBITE_OUTLINE_HEX)
val FrostbiteSurfaceVariant = Color(FROSTBITE_VARIANT_HEX)
val FrostbiteSurface = Color(FROSTBITE_SURFACE_HEX)
val FrostbiteBackground = Color(FROSTBITE_BG_HEX)

val frostbiteColors = AureoleColors(
    onSurfaceHigh = FrostbiteOnSurfaceHigh,
    onSurfaceMedium = FrostbiteOnSurfaceMedium,
    onSurfaceLow = FrostbiteOnSurfaceLow,
    outline = FrostbiteOutline,
    surfaceVariant = FrostbiteSurfaceVariant,
    surface = FrostbiteSurface,
    background = FrostbiteBackground
)
