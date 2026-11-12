package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import dev.mnascimentos.aureole.core.search.normalizeForSearch

/** Letter of the scrubber that gathers every label that does not start with a letter. */
const val NON_LETTER_BUCKET = '#'

/** Test tag of the browse grid, where the hold-and-slide gesture lives. */
const val FOLDER_PICKER_GRID_TEST_TAG = "folder_picker_grid"

private const val SELECTED_ROW_KEY_PREFIX = "sel_"
private const val REMAINING_CELL_KEY_PREFIX = "rem_"

/** Lazy key of the full width row of a selected app. */
fun selectedRowKey(pkg: String): String = SELECTED_ROW_KEY_PREFIX + pkg

/** Lazy key of the grid cell of an app that is not in the folder. */
fun remainingCellKey(pkg: String): String = REMAINING_CELL_KEY_PREFIX + pkg

/** Package behind a picker lazy key, or null for headers and any other item. */
fun packageOfPickerKey(key: Any): String? {
    val text = key as? String ?: return null
    return when {
        text.startsWith(SELECTED_ROW_KEY_PREFIX) -> text.removePrefix(SELECTED_ROW_KEY_PREFIX)
        text.startsWith(REMAINING_CELL_KEY_PREFIX) -> text.removePrefix(REMAINING_CELL_KEY_PREFIX)
        else -> null
    }
}

/**
 * Bounds of one visible lazy item, in pixels relative to the top start of the grid, as
 * `LazyGridState.layoutInfo.visibleItemsInfo` gives them.
 */
data class PickerCellBounds(
    val key: Any,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

/** Package of the app under ([x], [y]), or null when the point is on a header, a gap or outside the grid. */
fun hitTestCell(x: Float, y: Float, visibleItems: List<PickerCellBounds>): String? {
    val hit = visibleItems.firstOrNull { item ->
        x >= item.x && x < item.x + item.width && y >= item.y && y < item.y + item.height
    } ?: return null
    return packageOfPickerKey(hit.key)
}

/**
 * How far to scroll this frame while a selection drag sits near an edge: negative near the top, positive
 * near the bottom, growing up to [maxStepPx] as the finger goes deeper into the [edgePx] band.
 */
fun edgeAutoScrollDelta(y: Float, viewportHeight: Float, edgePx: Float, maxStepPx: Float): Float {
    if (viewportHeight <= 0f || edgePx <= 0f) return 0f
    val bottomEdge = viewportHeight - edgePx
    return when {
        y < edgePx -> -maxStepPx * ((edgePx - y) / edgePx).coerceAtMost(1f)
        y > bottomEdge -> maxStepPx * ((y - bottomEdge) / edgePx).coerceAtMost(1f)
        else -> 0f
    }
}

/** Scrubber letter of [label]: its first letter without accents, or [NON_LETTER_BUCKET]. */
fun scrubberLetterOf(label: String): Char {
    val first = normalizeForSearch(label.trim()).firstOrNull() ?: return NON_LETTER_BUCKET
    return if (first.isLetter()) first.uppercaseChar() else NON_LETTER_BUCKET
}

/**
 * First lazy index of each scrubber letter in [items], in the order the letters first appear. [offset] is
 * the lazy index of the first item, so the result can be fed straight to `scrollToItem`.
 */
fun <T> letterIndex(items: List<T>, offset: Int = 0, label: (T) -> String): Map<Char, Int> {
    val index = LinkedHashMap<Char, Int>()
    items.forEachIndexed { position, item ->
        index.getOrPut(scrubberLetterOf(label(item))) { offset + position }
    }
    return index
}

/** Order of the apps in the grid: alphabetical, ignoring case and accents. */
fun <T> sortedForPickerGrid(items: List<T>, label: (T) -> String): List<T> =
    items.sortedBy { normalizeForSearch(label(it)) }

/**
 * Lazy index of the first grid cell: the selected header, the selected rows (or their empty message) and
 * the header of the other apps come before it.
 */
fun firstGridCellIndex(selectedCount: Int): Int = 2 + selectedCount.coerceAtLeast(1)
