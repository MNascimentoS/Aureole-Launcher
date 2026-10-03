package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

private const val DEFAULT_CLOCK_SPAN_X = 20
private const val DEFAULT_CLOCK_SPAN_Y = 9
private const val DEFAULT_PANEL_COL = 14
private const val DEFAULT_PANEL_ROW = 9
private const val DEFAULT_PANEL_SPAN_X = 6
private const val DEFAULT_PANEL_SPAN_Y = 11
private const val DEFAULT_APPS_ROW = 13
private const val DEFAULT_APPS_SPAN_X = 14
private const val DEFAULT_APPS_SPAN_Y = 17

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
private const val DEFAULT_MIN_COL_SPAN = 1
private const val DEFAULT_MIN_ROW_SPAN = 1

object GridDefaults {
    fun getDefaultSpanForType(type: LauncherItemType): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return when (type) {
            LauncherItemType.CLOCK -> Pair(
                Pair(CLOCK_DEFAULT_COL_SPAN, CLOCK_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.APPS_LIST -> Pair(
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
        }
    }

    fun getDefaultGridItems(isLandscape: Boolean = false): List<LauncherItemState> {
        return if (isLandscape) {
            getDefaultLandscapeGridItems()
        } else {
            getDefaultPortraitGridItems()
        }
    }

    private fun getDefaultLandscapeGridItems(): List<LauncherItemState> {
        return listOf(
            LauncherItemState(
                id = "clock_item",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 0,
                colSpan = 20,
                rowSpan = 6,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "container_item",
                type = LauncherItemType.SHORTCUTS_CONTAINER,
                col = 24,
                row = 0,
                colSpan = 6,
                rowSpan = 20,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "apps_list_item",
                type = LauncherItemType.APPS_LIST,
                col = 0,
                row = 6,
                colSpan = 24,
                rowSpan = 14,
                minColSpan = 1,
                minRowSpan = 1
            )
        )
    }

    private fun getDefaultPortraitGridItems(): List<LauncherItemState> {
        return listOf(
            LauncherItemState(
                id = "clock_item",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 1,
                colSpan = DEFAULT_CLOCK_SPAN_X,
                rowSpan = DEFAULT_CLOCK_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "container_item",
                type = LauncherItemType.SHORTCUTS_CONTAINER,
                col = DEFAULT_PANEL_COL,
                row = DEFAULT_PANEL_ROW,
                colSpan = DEFAULT_PANEL_SPAN_X,
                rowSpan = DEFAULT_PANEL_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "apps_list_item",
                type = LauncherItemType.APPS_LIST,
                col = 0,
                row = DEFAULT_APPS_ROW,
                colSpan = DEFAULT_APPS_SPAN_X,
                rowSpan = DEFAULT_APPS_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            )
        )
    }
}
