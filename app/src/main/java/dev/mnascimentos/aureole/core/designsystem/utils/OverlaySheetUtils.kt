package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds

/**
 * Closes the overlay on a tap outside the sheet. A plain tap detector instead of `clickable`, so the
 * scrim does not merge the whole sheet into a single accessibility node; TalkBack closes with back.
 */
internal fun Modifier.scrimTapToDismiss(onDismiss: () -> Unit): Modifier {
    return pointerInput(onDismiss) { detectTapGestures(onTap = { onDismiss() }) }
}

/**
 * How far the sheet is from rest (0) to expanded (1); read while drawing, so a drag does not recompose. Stays at
 * 0 while a sheet that cannot expand stretches, so the preview above it does not fade.
 */
internal fun expandFractionOf(motion: OverlaySheetMotion, isExpanded: Boolean, bounds: SheetBounds): Float {
    val currentHeight = motion.currentHeight(isExpanded, bounds)
    val restHeight = motion.restOrFallback(bounds)
    val expandedHeight = motion.expandedHeight(bounds) ?: return 0f
    if (expandedHeight <= restHeight) return 0f
    return ((currentHeight - restHeight) / (expandedHeight - restHeight)).coerceIn(0f, 1f)
}

/**
 * Whether an [AureoleEditOverlay][dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay]
 * leaves its dimmed, blurred backdrop to the screen around it. A screen that swaps one sheet for another
 * provides true and draws one backdrop for both, so it does not flash between them.
 */
val LocalOverlayBackdropProvided = staticCompositionLocalOf { false }
