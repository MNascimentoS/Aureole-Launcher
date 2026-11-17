package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

/**
 * Widest an edit overlay sheet gets, centered on the screen. A screen that hosts several sheets (the settings) sets
 * it once for all of them, so in landscape they do not stretch from edge to edge. Unspecified: full width.
 */
val LocalOverlaySheetMaxWidth = staticCompositionLocalOf { Dp.Unspecified }

/**
 * True when the screen around draws the dimmed backdrop of every overlay it hosts, so swapping one sheet for another
 * does not flash the screen behind. Same as `AureoleEditOverlayConfig.hasExternalBackdrop`, for a whole subtree.
 */
val LocalOverlayExternalBackdrop = staticCompositionLocalOf { false }
