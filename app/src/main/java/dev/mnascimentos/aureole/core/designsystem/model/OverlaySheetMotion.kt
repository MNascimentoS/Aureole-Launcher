package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Live height of the overlay sheet, in pixels.
 *
 * At rest the sheet hugs its content and [pinnedHeight] is null. While a finger moves it, or while it
 * settles, [pinnedHeight] holds the exact height so the sheet follows the finger.
 */
internal class OverlaySheetMotion {
    var pinnedHeight by mutableStateOf<Float?>(null)

    /** Height the sheet measured last time it hugged its content. Zero until it is measured. */
    var restHeight = 0f

    /** Height of the whole sheet slot at rest, bottom inset included. Sizes the preview above it. */
    var restOuterHeight = 0

    var settleJob: Job? = null

    fun restOrFallback(bounds: SheetBounds): Float {
        return if (restHeight > 0f) restHeight.coerceAtMost(bounds.restMaxPx) else bounds.restMaxPx
    }

    fun currentHeight(isExpanded: Boolean, bounds: SheetBounds): Float {
        return pinnedHeight ?: settledHeight(isExpanded, bounds)
    }

    fun settledHeight(isExpanded: Boolean, bounds: SheetBounds): Float {
        return if (isExpanded) bounds.expandedPx else restOrFallback(bounds)
    }

    /** Fixed height for the sheet layout, or null when it hugs its content (at rest). */
    fun fixedHeight(isExpanded: Boolean, bounds: SheetBounds): Float? {
        val pinned = pinnedHeight
        return when {
            pinned != null -> pinned.coerceAtLeast(restOrFallback(bounds))
            isExpanded -> bounds.expandedPx
            else -> null
        }
    }

    /** How far the sheet slides down below its rest height while it is dragged to close. */
    fun translation(bounds: SheetBounds): Float {
        val pinned = pinnedHeight ?: return 0f
        return (restOrFallback(bounds) - pinned).coerceAtLeast(0f)
    }

    /**
     * Moves the sheet by a finger [delta] (positive = down) and returns whether it crossed a detent.
     */
    fun dragBy(delta: Float, isExpanded: Boolean, bounds: SheetBounds): Boolean {
        val previous = currentHeight(isExpanded, bounds)
        val next = (previous - delta).coerceIn(0f, bounds.expandedPx)
        pinnedHeight = next
        return crossedDetent(previous, next, listOf(restOrFallback(bounds), bounds.expandedPx))
    }

    fun release(velocity: Float, isExpanded: Boolean, bounds: SheetBounds): SheetDetentTarget {
        return resolveDetent(
            height = currentHeight(isExpanded, bounds),
            rest = restOrFallback(bounds),
            expanded = bounds.expandedPx,
            velocity = velocity
        )
    }

    suspend fun animateHeight(from: Float, to: Float) {
        animate(
            initialValue = from,
            targetValue = to,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) { value, _ -> pinnedHeight = value }
        pinnedHeight = null
    }
}

/**
 * Gesture entry points of the overlay sheet: drag, nested scroll, double tap and accessibility actions
 * all end up here, so they snap, tick and close the same way.
 *
 * @param isExpanded Whether the sheet is expanded when the controller is created.
 * @param density Screen density, to turn pixel velocities into dp per second.
 */
internal class OverlaySheetController(
    private val motion: OverlaySheetMotion,
    private val bounds: SheetBounds,
    isExpanded: Boolean,
    private val events: OverlaySheetEvents,
    private val scope: CoroutineScope,
    private val density: Float,
) {
    private var expanded = isExpanded

    val isExpanded: Boolean get() = expanded

    /** True while the finger holds the sheet at a height of its own. */
    val isHeld: Boolean get() = motion.pinnedHeight != null && motion.settleJob?.isActive != true

    fun onDragStarted() {
        motion.settleJob?.cancel()
    }

    fun onDrag(delta: Float) {
        if (motion.dragBy(delta, expanded, bounds)) events.onTick()
    }

    /**
     * Resizes the sheet from a nested scroll [delta] (positive = down) and returns the part of it the
     * sheet used, with the same sign as [delta].
     */
    fun scrollBy(delta: Float): Float {
        motion.settleJob?.cancel()
        val previous = motion.currentHeight(expanded, bounds)
        onDrag(delta)
        return previous - motion.currentHeight(expanded, bounds)
    }

    fun currentHeight(): Float = motion.currentHeight(expanded, bounds)

    fun expandedHeight(): Float = bounds.expandedPx

    /** Settles the sheet after the finger lifts with [velocityPx] pixels per second (positive = down). */
    fun onRelease(velocityPx: Float) {
        val velocity = if (density > 0f) velocityPx / density else velocityPx
        when (motion.release(velocity, expanded, bounds)) {
            SheetDetentTarget.Dismiss -> events.onDismiss()
            SheetDetentTarget.Expanded -> settleTo(expand = true)
            SheetDetentTarget.Rest -> settleTo(expand = false)
        }
    }

    fun toggle() {
        settleTo(expand = !expanded)
    }

    fun settleTo(expand: Boolean) {
        val from = motion.currentHeight(expanded, bounds)
        expanded = expand
        events.onExpandedChanged(expand)
        events.onTick()
        val to = motion.settledHeight(expand, bounds)
        motion.settleJob?.cancel()
        motion.settleJob = scope.launch { motion.animateHeight(from, to) }
    }
}

/**
 * What [OverlaySheetController] reports back to the overlay.
 *
 * @property onExpandedChanged The sheet moved to the expanded (true) or rest (false) height.
 * @property onDismiss The sheet was released low or flung down at rest and should close.
 * @property onTick The sheet crossed or settled on a detent: play the segment tick vibration.
 */
internal data class OverlaySheetEvents(
    val onExpandedChanged: (Boolean) -> Unit,
    val onDismiss: () -> Unit,
    val onTick: () -> Unit
)
