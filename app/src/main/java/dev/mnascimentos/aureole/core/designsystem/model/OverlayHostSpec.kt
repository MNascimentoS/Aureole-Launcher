package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.ui.unit.Dp

/**
 * What a screen that hosts several overlays (the settings) decides for all of them; the default leaves an overlay
 * as it is on its own.
 *
 * @property maxSheetWidth Widest an overlay sheet gets, centered on the screen, so in landscape the sheets do not
 * stretch from edge to edge. Unspecified: full width.
 * @property hasExternalBackdrop True when the screen around draws the dimmed backdrop of every overlay it hosts,
 * so swapping one sheet for another does not flash the screen behind.
 * @property isTallInLandscape True when, in landscape, a sheet with no preview may rest as tall as one with a
 * preview (it still hugs its content when shorter), so a long list is not squeezed into a short sheet.
 * @property showHeaderAction False leaves the logo (the shortcut to the settings) out of every overlay it hosts: inside
 * the settings it would only lead back to where the person already is.
 */
data class OverlayHostSpec(
    val maxSheetWidth: Dp = Dp.Unspecified,
    val hasExternalBackdrop: Boolean = false,
    val isTallInLandscape: Boolean = false,
    val showHeaderAction: Boolean = true
)
