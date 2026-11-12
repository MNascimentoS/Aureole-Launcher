package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.foundation.gestures.detectTapGestures
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

/** How far the sheet is from rest (0) to expanded (1); read while drawing, so a drag does not recompose. */
internal fun expandFractionOf(motion: OverlaySheetMotion, isExpanded: Boolean, bounds: SheetBounds): Float {
    val currentHeight = motion.currentHeight(isExpanded, bounds)
    val restHeight = motion.restOrFallback(bounds)
    if (bounds.expandedPx <= restHeight) return 0f
    return ((currentHeight - restHeight) / (bounds.expandedPx - restHeight)).coerceIn(0f, 1f)
}
