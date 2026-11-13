package dev.mnascimentos.aureole.composable.reorder.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Drag of one item in a reorderable list. [start] picks the item, [drag] adds the finger's movement along the main
 * axis and reports a [ReorderMove] when the item passes a neighbour, [release] lets it go and [settle]/[clear]
 * bring it back into its slot. [offset] is how far the item is drawn from its slot.
 */
@Stable
class ReorderDragState {
    /** Key of the item being dragged or settling back into place; null when the list is at rest. */
    var activeKey by mutableStateOf<Any?>(null)
        private set

    /** True while the finger holds an item. */
    var isDragging by mutableStateOf(false)
        private set

    var offset by mutableFloatStateOf(0f)
        private set

    private val sizes = mutableMapOf<Any, Float>()
    private var order: List<Any> = emptyList()
    private var index = -1
    private var hasMoved = false

    /** Records the main-axis size of the item with [key]. */
    fun onMeasured(key: Any, size: Float) {
        sizes[key] = size
    }

    /** Starts dragging [key] out of [keys]; false (and nothing changes) when it is not in the list. */
    fun start(key: Any, keys: List<Any>): Boolean {
        val startIndex = keys.indexOf(key)
        if (startIndex < 0) return false
        activeKey = key
        isDragging = true
        offset = 0f
        order = keys
        index = startIndex
        hasMoved = false
        return true
    }

    /** Moves the dragged item by [delta]; the swap to report, if the item passed a neighbour. */
    fun drag(delta: Float, spacing: Float): ReorderMove? {
        val key = activeKey
        if (!isDragging || key == null) return null
        offset += delta
        val fallback = sizes[key] ?: 0f
        val slotSizes = order.map { sizes[it] ?: fallback }
        val target = reorderTargetIndex(index, offset, slotSizes, spacing)
        return if (target == index) null else swapTo(target, slotSizes, spacing)
    }

    /**
     * The list scrolled [consumed] px (positive: towards its end) under the held item: moves the item as much, so it
     * stays under the finger and the swap check sees the scroll too; the swap to report, if any.
     */
    fun onScrolled(consumed: Float, spacing: Float): ReorderMove? = drag(consumed, spacing)

    private fun swapTo(target: Int, slotSizes: List<Float>, spacing: Float): ReorderMove {
        offset -= reorderShift(index, target, slotSizes, spacing)
        val swap = ReorderMove(index, target)
        order = move(order, index, target)
        index = target
        hasMoved = true
        return swap
    }

    /** Lets the item go; true when it changed place during the drag. */
    fun release(): Boolean {
        val moved = isDragging && hasMoved
        isDragging = false
        hasMoved = false
        return moved
    }

    /** Draws the released item [value] away from its slot while it settles. Ignored once a new drag started. */
    fun settle(key: Any, value: Float) {
        if (!isDragging && activeKey == key) offset = value
    }

    /** Ends the settling of [key], leaving the list at rest. Ignored once a new drag started. */
    fun clear(key: Any) {
        if (isDragging || activeKey != key) return
        activeKey = null
        offset = 0f
    }
}
