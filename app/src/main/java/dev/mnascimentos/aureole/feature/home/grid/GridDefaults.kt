package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

private const val CLOCK_DEFAULT_COL_SPAN = 20
private const val CLOCK_DEFAULT_ROW_SPAN = 9
private const val APPS_DEFAULT_COL_SPAN = 14
private const val APPS_DEFAULT_ROW_SPAN = 17
private const val CONTAINER_DEFAULT_COL_SPAN = 6
private const val CONTAINER_DEFAULT_ROW_SPAN = 11
private const val WIDGET_DEFAULT_COL_SPAN = 14
private const val WIDGET_DEFAULT_ROW_SPAN = 6
private const val WIDGET_LIST_DEFAULT_COL_SPAN = 14
private const val WIDGET_LIST_DEFAULT_ROW_SPAN = 6
private const val SCROLL_VIEW_DEFAULT_COL_SPAN = 14
private const val SCROLL_VIEW_DEFAULT_ROW_SPAN = 9
private const val QUICK_BUTTON_DEFAULT_COL_SPAN = 4
private const val QUICK_BUTTON_DEFAULT_ROW_SPAN = 3
private const val DEFAULT_MIN_COL_SPAN = 1
private const val DEFAULT_MIN_ROW_SPAN = 1

// Default layout v3 (specs/first-user-experience/default-layout-v3.png), right-handed.
// Left-handed mode mirrors every item horizontally.
private const val PORTRAIT_FULL_WIDTH = 20
private const val PORTRAIT_CLOCK_ROW_SPAN = 6
private const val PORTRAIT_WIDGETS_ROW = 6
private const val PORTRAIT_WIDGETS_ROW_SPAN = 6
private const val PORTRAIT_BOTTOM_ROW = 12
private const val PORTRAIT_BOTTOM_ROW_SPAN = 18
private const val PORTRAIT_APPS_COL_SPAN = 16
private const val PORTRAIT_CONTAINER_COL = 16
private const val PORTRAIT_CONTAINER_COL_SPAN = 4

private const val LANDSCAPE_CLOCK_COL_SPAN = 14
private const val LANDSCAPE_CLOCK_ROW_SPAN = 7
private const val LANDSCAPE_WIDGETS_COL = 14
private const val LANDSCAPE_WIDGETS_COL_SPAN = 12
private const val LANDSCAPE_FULL_HEIGHT = 20
private const val LANDSCAPE_APPS_ROW = 7
private const val LANDSCAPE_APPS_COL_SPAN = 14
private const val LANDSCAPE_APPS_ROW_SPAN = 13
private const val LANDSCAPE_CONTAINER_COL = 26
private const val LANDSCAPE_CONTAINER_COL_SPAN = 4

object GridDefaults {
    fun getDefaultSpanForType(type: LauncherItemType): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return when (type) {
            LauncherItemType.CLOCK -> Pair(
                Pair(CLOCK_DEFAULT_COL_SPAN, CLOCK_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.APPS_LIST, LauncherItemType.ALL_APPS_LIST -> Pair(
                Pair(APPS_DEFAULT_COL_SPAN, APPS_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SHORTCUTS_CONTAINER -> Pair(
                Pair(CONTAINER_DEFAULT_COL_SPAN, CONTAINER_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SINGLE_APP_WIDGET -> Pair(
                Pair(WIDGET_DEFAULT_COL_SPAN, WIDGET_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.WIDGET_LIST -> Pair(
                Pair(WIDGET_LIST_DEFAULT_COL_SPAN, WIDGET_LIST_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SCROLL_VIEW -> Pair(
                Pair(SCROLL_VIEW_DEFAULT_COL_SPAN, SCROLL_VIEW_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.QUICK_BUTTON -> Pair(
                Pair(QUICK_BUTTON_DEFAULT_COL_SPAN, QUICK_BUTTON_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
        }
    }

    const val CLOCK_ITEM_ID = "clock_item"
    const val WIDGETS_ITEM_ID = "widgets_item"
    const val APPS_LIST_ITEM_ID = "apps_list_item"
    const val CONTAINER_ITEM_ID = "container_item"

    /** Without favorites, the apps spot holds the all apps list, so the home always shows apps. */
    fun getDefaultGridItems(
        isLandscape: Boolean = false,
        isLeftHanded: Boolean = false,
        hasFavorites: Boolean = true
    ): List<LauncherItemState> {
        val items = (if (isLandscape) landscapeItems() else portraitItems()).map { item ->
            val showsAllApps = !hasFavorites && item.safeType == LauncherItemType.APPS_LIST
            if (showsAllApps) item.copy(type = LauncherItemType.ALL_APPS_LIST) else item
        }
        if (!isLeftHanded) return items
        val maxCols = if (isLandscape) GridEngineUtils.LANDSCAPE_MAX_COLS else GridEngineUtils.PORTRAIT_MAX_COLS
        return items.map { it.copy(col = maxCols - it.col - it.colSpan) }
    }

    /** The item of [type] in the default layout, or null when the layout has none. */
    fun getDefaultItem(
        type: LauncherItemType,
        isLandscape: Boolean,
        isLeftHanded: Boolean
    ): LauncherItemState? = getDefaultGridItems(
        isLandscape = isLandscape,
        isLeftHanded = isLeftHanded,
        hasFavorites = type != LauncherItemType.ALL_APPS_LIST
    ).firstOrNull { it.safeType == type }

    private fun portraitItems(): List<LauncherItemState> = listOf(
        LauncherItemType.CLOCK.placedAt(CLOCK_ITEM_ID, 0, 0, PORTRAIT_FULL_WIDTH, PORTRAIT_CLOCK_ROW_SPAN),
        LauncherItemType.WIDGET_LIST.placedAt(
            WIDGETS_ITEM_ID,
            0,
            PORTRAIT_WIDGETS_ROW,
            PORTRAIT_FULL_WIDTH,
            PORTRAIT_WIDGETS_ROW_SPAN
        ),
        LauncherItemType.APPS_LIST.placedAt(
            APPS_LIST_ITEM_ID,
            0,
            PORTRAIT_BOTTOM_ROW,
            PORTRAIT_APPS_COL_SPAN,
            PORTRAIT_BOTTOM_ROW_SPAN
        ),
        LauncherItemType.SHORTCUTS_CONTAINER.placedAt(
            CONTAINER_ITEM_ID,
            PORTRAIT_CONTAINER_COL,
            PORTRAIT_BOTTOM_ROW,
            PORTRAIT_CONTAINER_COL_SPAN,
            PORTRAIT_BOTTOM_ROW_SPAN
        )
    )

    private fun landscapeItems(): List<LauncherItemState> = listOf(
        LauncherItemType.CLOCK.placedAt(CLOCK_ITEM_ID, 0, 0, LANDSCAPE_CLOCK_COL_SPAN, LANDSCAPE_CLOCK_ROW_SPAN),
        LauncherItemType.WIDGET_LIST.placedAt(
            WIDGETS_ITEM_ID,
            LANDSCAPE_WIDGETS_COL,
            0,
            LANDSCAPE_WIDGETS_COL_SPAN,
            LANDSCAPE_FULL_HEIGHT
        ),
        LauncherItemType.APPS_LIST.placedAt(
            APPS_LIST_ITEM_ID,
            0,
            LANDSCAPE_APPS_ROW,
            LANDSCAPE_APPS_COL_SPAN,
            LANDSCAPE_APPS_ROW_SPAN
        ),
        LauncherItemType.SHORTCUTS_CONTAINER.placedAt(
            CONTAINER_ITEM_ID,
            LANDSCAPE_CONTAINER_COL,
            0,
            LANDSCAPE_CONTAINER_COL_SPAN,
            LANDSCAPE_FULL_HEIGHT
        )
    )

    private fun LauncherItemType.placedAt(
        id: String,
        col: Int,
        row: Int,
        colSpan: Int,
        rowSpan: Int
    ) = LauncherItemState(
        id = id,
        type = this,
        col = col,
        row = row,
        colSpan = colSpan,
        rowSpan = rowSpan,
        minColSpan = DEFAULT_MIN_COL_SPAN,
        minRowSpan = DEFAULT_MIN_ROW_SPAN
    )
}
