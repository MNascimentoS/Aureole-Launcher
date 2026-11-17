package dev.mnascimentos.aureole.composable.appitem.model

import androidx.compose.ui.graphics.Color

/** Opacity of the tile behind a themed icon. */
private const val THEMED_BOX_ALPHA = 0.85f

/** Share of the icon size left as padding between the tile and the themed glyph. */
const val THEMED_BOX_PADDING_RATIO = 0.19f

/**
 * Look of a themed app icon, the one rule shared by the home and the previews: a tile in the surface variant color
 * with the monochrome glyph tinted on top.
 *
 * @property tile Fill of the tile behind the glyph.
 * @property tint Color of the glyph.
 */
data class ThemedIconStyle(val tile: Color, val tint: Color)

/** The themed icon look for a theme with these colors. */
fun themedIconStyleOf(surfaceVariant: Color, onSurfaceMedium: Color): ThemedIconStyle =
    ThemedIconStyle(tile = surfaceVariant.copy(alpha = THEMED_BOX_ALPHA), tint = onSurfaceMedium)
