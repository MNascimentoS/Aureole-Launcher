package dev.mnascimentos.aureole.composable.reorder.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Callbacks of a reorderable list. [onMove] runs at every swap while the item is dragged (and once per
 * accessibility "move" action): apply it to the list you pass back. [onMoveEnd] runs once when the gesture that
 * moved the item ends, the place to persist the order or offer "Undo". [onRemove] draws the ✕ badge on each item;
 * null hides it.
 */
data class ReorderableActions(
    val onMove: (from: Int, to: Int) -> Unit,
    val onMoveEnd: () -> Unit = {},
    val onRemove: ((index: Int) -> Unit)? = null,
)

/** Items of a reorderable list, the stable [key] of each one, its [actions] and the layout around them. */
data class ReorderableParams<T>(
    val items: List<T>,
    val key: (T) -> Any,
    val actions: ReorderableActions,
    val spacing: Dp = 0.dp,
    val contentPadding: PaddingValues = PaddingValues(),
    /** Only a drag handle placed by the item (see [LocalReorderHandle]) starts the drag, not the whole item. */
    val dragHandleOnly: Boolean = false,
)

/** One swap reported while dragging: the item at [from] now sits at [to]. */
data class ReorderMove(val from: Int, val to: Int)

/** Main axis of a reorderable list. */
enum class ReorderOrientation { Horizontal, Vertical }

/** What one item of a reorderable list needs to know about its place in it. */
data class ReorderSlotParams(
    val key: Any,
    val index: Int,
    val keys: List<Any>,
    val orientation: ReorderOrientation,
    val spacingPx: Float,
    val handleOnly: Boolean = false,
    /** Scrolls the list while the held item is near an edge of its viewport; null: no auto scroll. */
    val autoScroll: ReorderAutoScroll? = null,
)

/**
 * What an item of a list with [ReorderableParams.dragHandleOnly] needs to draw its handle: the [gestureModifier] that
 * starts the drag (hold, then drag) and whether this item is [isDragging] now.
 */
data class ReorderHandle(val gestureModifier: Modifier = Modifier, val isDragging: Boolean = false)

/** The [ReorderHandle] of the item being composed; the default one does nothing. */
val LocalReorderHandle = compositionLocalOf { ReorderHandle() }
