package dev.mnascimentos.aureole.composable.appitem.model

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * Options of [dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon].
 *
 * @param isThemed Draws the monochrome themed icon.
 * @param showThemedBox Puts the themed icon on a tile with the small corner radius.
 * @param legacyShape Clip kept for icons that are not full bleed (legacy bitmaps), so they look as before.
 * @param contentDescription Accessibility label; null when the row already announces the app name.
 */
data class AureoleAppIconParams(
    val isThemed: Boolean = false,
    val showThemedBox: Boolean = true,
    val legacyShape: Shape = RectangleShape,
    val contentDescription: String? = null
)
