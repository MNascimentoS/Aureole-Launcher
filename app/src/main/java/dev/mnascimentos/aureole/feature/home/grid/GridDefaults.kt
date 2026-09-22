package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

private const val DEFAULT_CLOCK_SPAN_X = 10
private const val DEFAULT_CLOCK_SPAN_Y = 6
private const val DEFAULT_PANEL_COL = 7
private const val DEFAULT_PANEL_ROW = 7
private const val DEFAULT_PANEL_SPAN_X = 3
private const val DEFAULT_PANEL_SPAN_Y = 7
private const val DEFAULT_APPS_ROW = 9
private const val DEFAULT_APPS_SPAN_X = 7
private const val DEFAULT_APPS_SPAN_Y = 11

private const val CLOCK_DEFAULT_COL_SPAN = 10
private const val CLOCK_DEFAULT_ROW_SPAN = 6
private const val APPS_DEFAULT_COL_SPAN = 7
private const val APPS_DEFAULT_ROW_SPAN = 11
private const val SIDE_PANEL_DEFAULT_COL_SPAN = 3
private const val SIDE_PANEL_DEFAULT_ROW_SPAN = 7
private const val WIDGET_DEFAULT_COL_SPAN = 2
private const val WIDGET_DEFAULT_ROW_SPAN = 2
private const val WIDGET_LIST_DEFAULT_COL_SPAN = 3
private const val WIDGET_LIST_DEFAULT_ROW_SPAN = 2
private const val SCROLL_VIEW_DEFAULT_COL_SPAN = 7
private const val SCROLL_VIEW_DEFAULT_ROW_SPAN = 6
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
            LauncherItemType.SHORTCUTS_SIDE_PANEL -> Pair(
                Pair(SIDE_PANEL_DEFAULT_COL_SPAN, SIDE_PANEL_DEFAULT_ROW_SPAN),
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
                colSpan = 10,
                rowSpan = 4,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "side_panel_item",
                type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
                col = 17,
                row = 0,
                colSpan = 3,
                rowSpan = 10,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "apps_list_item",
                type = LauncherItemType.APPS_LIST,
                col = 0,
                row = 4,
                colSpan = 16,
                rowSpan = 6,
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
                id = "side_panel_item",
                type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
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
