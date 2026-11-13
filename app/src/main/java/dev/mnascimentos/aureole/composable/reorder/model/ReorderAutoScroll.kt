package dev.mnascimentos.aureole.composable.reorder.model

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Job

/** Longest frame the auto scroll accounts for, so a hitch never makes the list jump. */
private const val MAX_FRAME_SECONDS = 0.05f

/**
 * Scroll, in px, for one frame of auto scroll while an item is dragged.
 *
 * [pointer] is the finger along the main axis, measured from the start of the viewport (in reading order: from
 * the right in right-to-left rows) and [viewport] the viewport's size on that axis. Inside the band of [edge] px
 * at either end the list scrolls towards that end, faster the closer the finger is to it: from 0 at the inner
 * border of the band to [maxSpeed] px/s at the edge and beyond. A viewport smaller than two bands splits in half,
 * so its centre never scrolls. Negative: towards the start; positive: towards the end.
 */
fun reorderAutoScrollDelta(pointer: Float, viewport: Float, edge: Float, maxSpeed: Float, frameSeconds: Float): Float {
    if (viewport <= 0f || edge <= 0f || maxSpeed <= 0f) return 0f
    val band = minOf(edge, viewport / 2f)
    val intoStartBand = band - pointer
    val intoEndBand = pointer - (viewport - band)
    val proximity = when {
        intoStartBand > 0f -> -intoStartBand / band
        intoEndBand > 0f -> intoEndBand / band
        else -> 0f
    }.coerceIn(-1f, 1f)
    return proximity * maxSpeed * frameSeconds.coerceIn(0f, MAX_FRAME_SECONDS)
}

/** [delta], or 0 when the list cannot scroll that way any more (or does not scroll at all). */
fun reorderAllowedScroll(delta: Float, canScrollBackward: Boolean, canScrollForward: Boolean): Float = when {
    delta < 0f && !canScrollBackward -> 0f
    delta > 0f && !canScrollForward -> 0f
    else -> delta
}

/**
 * Where [position] (relative to the top left corner of a viewport of [size]) sits along the main axis, measured
 * from the start of the list: from the top in columns, from the left or (right-to-left) the right in rows.
 */
fun reorderAxisPosition(position: Offset, size: IntSize, orientation: ReorderOrientation, isRtl: Boolean): Float =
    when {
        orientation == ReorderOrientation.Vertical -> position.y
        isRtl -> size.width - position.x
        else -> position.x
    }

/** Part of a finger movement [amount] along the main axis, positive towards the end of the list. */
fun reorderAxisDelta(amount: Offset, orientation: ReorderOrientation, isRtl: Boolean): Float = when {
    orientation == ReorderOrientation.Vertical -> amount.y
    isRtl -> -amount.x
    else -> amount.x
}

/** Main-axis size of a viewport of [size]. */
fun reorderAxisSize(size: IntSize, orientation: ReorderOrientation): Float =
    if (orientation == ReorderOrientation.Vertical) size.height.toFloat() else size.width.toFloat()

/**
 * Auto scroll of a reorderable list: scrolls [scrollable] while the dragged item is held near an edge of its
 * viewport. `ReorderableRow` makes its own; for a `ReorderableColumn` inside a scrolling container, make one with
 * `rememberReorderAutoScroll` for that container's state, mark the container with `reorderAutoScrollViewport` and
 * pass it to the column.
 */
@Stable
class ReorderAutoScroll(internal val scrollable: ScrollableState) {
    /** Layout of the viewport: the scrolling container itself, not its scrolled content. */
    internal var viewport: LayoutCoordinates? = null

    /** The scrolling loop, while the finger sits in an edge band. */
    internal var job: Job? = null

    private var pointer: Float? = null
    private var viewportSize = 0f

    /** Records the finger at [local], relative to [source] (the node that detects the drag). */
    fun track(source: LayoutCoordinates, local: Offset, orientation: ReorderOrientation, isRtl: Boolean) {
        val view = viewport?.takeIf { it.isAttached && source.isAttached } ?: return
        val inViewport = view.localPositionOf(source, local)
        pointer = reorderAxisPosition(inViewport, view.size, orientation, isRtl)
        viewportSize = reorderAxisSize(view.size, orientation)
    }

    /** Forgets the finger, once it lifts. */
    fun stop() {
        pointer = null
    }

    /** Scroll for a frame of [frameSeconds] at the last tracked finger; 0 when not near an edge or at that end. */
    fun frameDelta(edge: Float, maxSpeed: Float, frameSeconds: Float): Float {
        val finger = pointer ?: return 0f
        val delta = reorderAutoScrollDelta(finger, viewportSize, edge, maxSpeed, frameSeconds)
        return reorderAllowedScroll(delta, scrollable.canScrollBackward, scrollable.canScrollForward)
    }
}
