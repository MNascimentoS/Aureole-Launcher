package dev.mnascimentos.aureole.core.designsystem.model

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

    /**
     * Height the sheet would take to show all of its content, measured at rest; null while unknown or when the
     * content has no intrinsic height (lazy lists), which keeps the full expanded height.
     */
    var contentFullHeight by mutableStateOf<Float?>(null)

    /** Width and height (packed) the content had when [contentFullHeight] was measured; zero until then. */
    var contentMeasuredSize = 0L

    var settleJob: Job? = null

    /** Finger height behind the elastic stretch, and the stretched height it produced. */
    private var rawHeight: Float? = null
    private var stretchedFrom: Float? = null

    fun restOrFallback(bounds: SheetBounds): Float {
        return if (restHeight > 0f) restHeight.coerceAtMost(bounds.restMaxPx) else bounds.restMaxPx
    }

    /** Height the sheet expands to, or null when its content already fits at rest. */
    fun expandedHeight(bounds: SheetBounds): Float? {
        return expandTarget(restOrFallback(bounds), bounds.expandedPx, contentFullHeight, bounds.fitSlackPx)
    }

    fun canExpand(bounds: SheetBounds): Boolean = expandedHeight(bounds) != null

    fun currentHeight(isExpanded: Boolean, bounds: SheetBounds): Float {
        return pinnedHeight ?: settledHeight(isExpanded, bounds)
    }

    fun settledHeight(isExpanded: Boolean, bounds: SheetBounds): Float {
        val expanded = if (isExpanded) expandedHeight(bounds) else null
        return expanded ?: restOrFallback(bounds)
    }

    /** Fixed height for the sheet layout, or null when it hugs its content (at rest). */
    fun fixedHeight(isExpanded: Boolean, bounds: SheetBounds): Float? {
        val pinned = pinnedHeight
        return when {
            pinned != null -> pinned.coerceAtLeast(restOrFallback(bounds))
            isExpanded -> expandedHeight(bounds)
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
     *
     * When the content fits at rest the sheet cannot expand: above rest it stretches with resistance, up to
     * [SheetBounds.elasticLimitPx], and the stretched top is not a detent.
     */
    fun dragBy(delta: Float, isExpanded: Boolean, bounds: SheetBounds): Boolean {
        val previous = currentHeight(isExpanded, bounds)
        val rest = restOrFallback(bounds)
        val expanded = expandedHeight(bounds)
        val next = if (expanded != null) {
            (previous - delta).coerceIn(0f, expanded)
        } else {
            stretchBy(delta, previous, rest, bounds.elasticLimitPx)
        }
        pinnedHeight = next
        val detents = if (expanded != null) listOf(rest, expanded) else listOf(rest)
        return crossedDetent(previous, next, detents)
    }

    private fun stretchBy(delta: Float, previous: Float, rest: Float, limitPx: Float): Float {
        val raw = rawHeight?.takeIf { stretchedFrom == previous } ?: unstretchedHeight(previous, rest, limitPx)
        val nextRaw = (raw - delta).coerceAtLeast(0f)
        val next = stretchedHeight(nextRaw, rest, limitPx)
        rawHeight = nextRaw
        stretchedFrom = next
        return next
    }

    fun release(velocity: Float, isExpanded: Boolean, bounds: SheetBounds): SheetDetentTarget {
        val rest = restOrFallback(bounds)
        val expanded = expandedHeight(bounds)
        return resolveDetent(
            height = currentHeight(isExpanded, bounds),
            rest = rest,
            expanded = expanded ?: rest,
            velocity = velocity,
            canExpand = expanded != null
        )
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
        val wasArmed = motion.isDismissArmed(bounds)
        if (motion.dragBy(delta, expanded, bounds)) events.onTick()
        if (!wasArmed && motion.isDismissArmed(bounds)) events.onDismissArmed()
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

    /** Height the sheet grows to: the expanded one, or the rest height when the content already fits. */
    fun expandedHeight(): Float = motion.expandedHeight(bounds) ?: motion.restOrFallback(bounds)

    val canExpand: Boolean get() = motion.canExpand(bounds)

    /** Settles the sheet after the finger lifts with [velocityPx] pixels per second (positive = down). */
    fun onRelease(velocityPx: Float) {
        val velocity = if (density > 0f) velocityPx / density else velocityPx
        when (motion.release(velocity, expanded, bounds)) {
            SheetDetentTarget.Dismiss -> events.onDismiss()
            SheetDetentTarget.Expanded -> settleTo(expand = true)
            SheetDetentTarget.Rest -> settleAtRest(isFlingUp(velocity))
        }
    }

    private fun settleAtRest(isFlungUp: Boolean) {
        val isStretched = motion.currentHeight(expanded, bounds) > motion.restOrFallback(bounds)
        val needsSpringBack = isStretched || isFlungUp
        if (!expanded && !canExpand && needsSpringBack) {
            springBack(bounce = isFlungUp)
        } else {
            settleTo(expand = false)
        }
    }

    /** Double tap: expands or collapses; a sheet whose content already fits only bounces. */
    fun toggle() {
        if (!expanded && !motion.canExpand(bounds)) springBack(bounce = true) else settleTo(expand = !expanded)
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

    /** Back to rest from the elastic stretch, with no tick: the stretched top is not a detent. */
    private fun springBack(bounce: Boolean) {
        val from = motion.currentHeight(expanded, bounds)
        val rest = motion.restOrFallback(bounds)
        val peak = if (bounce) maxOf(from, rest + bounds.bouncePx) else from
        motion.settleJob?.cancel()
        motion.settleJob = scope.launch { motion.animateSpringBack(from, peak, rest) }
    }
}

/**
 * What [OverlaySheetController] reports back to the overlay.
 *
 * @property onExpandedChanged The sheet moved to the expanded (true) or rest (false) height.
 * @property onDismiss The sheet was released low or flung down at rest and should close.
 * @property onTick The sheet crossed or settled on a detent: play the segment tick vibration.
 * @property onDismissArmed A drag just took the sheet below the height where a release closes it.
 */
internal data class OverlaySheetEvents(
    val onExpandedChanged: (Boolean) -> Unit,
    val onDismiss: () -> Unit,
    val onTick: () -> Unit,
    val onDismissArmed: () -> Unit = {}
)
