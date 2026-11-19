package dev.mnascimentos.aureole.composable.icons.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCategory
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconEntry

/** Columns of the icon catalog grid. */
const val ICON_GRID_COLUMNS = 5

/** One cell of the catalog grid: the name of a category (spans the whole row) or one icon. */
sealed interface IconGridItem {
    data class Header(val category: AureoleIconCategory) : IconGridItem

    data class Icon(val entry: AureoleIconEntry) : IconGridItem
}

/** The grid cells: for each category its [IconGridItem.Header] followed by its icons, in catalog order. */
fun iconGridItems(entries: List<AureoleIconEntry> = AureoleIconCatalog.entries): List<IconGridItem> =
    AureoleIconCategory.entries.flatMap { category ->
        val icons = entries.filter { it.category == category }
        if (icons.isEmpty()) emptyList() else listOf(IconGridItem.Header(category)) + icons.map(IconGridItem::Icon)
    }

/** Categories that have icons, in the order of their headers (the chips). */
fun iconGridCategories(items: List<IconGridItem>): List<AureoleIconCategory> =
    items.filterIsInstance<IconGridItem.Header>().map { it.category }

/** Index of the header of [category] in [items]; 0 when it has none. */
fun iconGridHeaderIndex(items: List<IconGridItem>, category: AureoleIconCategory): Int =
    items.indexOfFirst { it is IconGridItem.Header && it.category == category }.coerceAtLeast(0)

/** Category the cell at [index] belongs to: the one of the nearest header at or above it. */
fun iconGridCategoryAt(items: List<IconGridItem>, index: Int): AureoleIconCategory? {
    val last = index.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val header = (last downTo 0).firstNotNullOfOrNull { (items.getOrNull(it) as? IconGridItem.Header)?.category }
    return header ?: (items.firstOrNull() as? IconGridItem.Icon)?.entry?.category
}

/** Cell the grid opens on: the header of the category of [selectedKey] (old keys included), else the first one. */
fun initialIconGridIndex(items: List<IconGridItem>, selectedKey: String?): Int {
    val category = AureoleIconCatalog.categoryOf(selectedKey) ?: return 0
    return iconGridHeaderIndex(items, category)
}

/** True when [entry] is the icon [selectedKey] stands for; an old key marks the icon it now resolves to. */
fun isIconSelected(entry: AureoleIconEntry, selectedKey: String?): Boolean =
    selectedKey != null && AureoleIconCatalog.resolveKey(selectedKey) == entry.key

/**
 * Pixels the chip row must scroll so the chip spanning [chipStart]..[chipEnd] sits entirely inside
 * [viewportStart]..[viewportEnd] (all in the row's coordinates). Zero when it is already inside; a chip wider than
 * the viewport lines up with the start.
 */
fun chipScrollDelta(chipStart: Int, chipEnd: Int, viewportStart: Int, viewportEnd: Int): Int = when {
    chipStart < viewportStart -> chipStart - viewportStart
    chipEnd > viewportEnd -> minOf(chipEnd - viewportEnd, chipStart - viewportStart)
    else -> 0
}
