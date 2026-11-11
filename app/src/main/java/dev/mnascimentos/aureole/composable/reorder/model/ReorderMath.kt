package dev.mnascimentos.aureole.composable.reorder.model

/** Share of a neighbour's slot the dragged item must cover before the two swap. */
private const val SWAP_THRESHOLD_FRACTION = 0.5f

/**
 * Index the dragged item should move to.
 *
 * [from] is where the item sits now, [offset] how far the finger moved it from that slot along the main axis
 * (negative: up/left in reading order, positive: down/right) and [sizes] the main-axis size of every item, in the
 * current order. A neighbour is passed once the item covers half of its slot (size + [spacing]). The result stays
 * inside the list, so dragging past either end only reaches the first or the last position.
 */
fun reorderTargetIndex(from: Int, offset: Float, sizes: List<Float>, spacing: Float): Int {
    if (from !in sizes.indices || offset == 0f) return from
    var target = from
    var remaining = offset
    val step = if (offset > 0f) 1 else -1
    val slot = { index: Int -> sizes[index] + spacing }
    // Signed: after a swap the leftover may point back, which must never trigger a swap the other way.
    while (target + step in sizes.indices && remaining * step >= slot(target + step) * SWAP_THRESHOLD_FRACTION) {
        remaining -= slot(target + step) * step
        target += step
    }
    return target
}

/**
 * How far the slot of the item moved from [from] to [to] travels along the main axis: the sum of the slots it
 * jumped over. Subtract it from the drag offset so the item stays under the finger after the swap.
 */
fun reorderShift(from: Int, to: Int, sizes: List<Float>, spacing: Float): Float {
    if (from == to || from !in sizes.indices || to !in sizes.indices) return 0f
    val range = if (to > from) (from + 1)..to else to until from
    val distance = range.sumOf { (sizes[it] + spacing).toDouble() }.toFloat()
    return if (to > from) distance else -distance
}

/** [list] with the element at [from] moved to [to]; the same list when either index is out of range. */
fun <T> move(list: List<T>, from: Int, to: Int): List<T> {
    if (from == to || from !in list.indices || to !in list.indices) return list
    return list.toMutableList().apply { add(to, removeAt(from)) }
}

/**
 * Target of an accessibility "move" action: one step towards the start ([towardsStart]) or the end of the list,
 * or null when the item is already at that end. For rows, "left" is towards the start in left-to-right layouts and
 * towards the end in right-to-left ones: pass `towardsStart = isLeft != isRtl`.
 */
fun reorderStepTarget(index: Int, size: Int, towardsStart: Boolean): Int? {
    val target = if (towardsStart) index - 1 else index + 1
    return target.takeIf { index in 0 until size && it in 0 until size }
}
