package dev.mnascimentos.aureole.core.data.model

import java.util.UUID

enum class LauncherItemType {
    CLOCK,
    APPS_LIST,
    SHORTCUTS_CONTAINER,
    SINGLE_APP_WIDGET,
    WIDGET_LIST,
    SCROLL_VIEW
}

enum class ScrollOrientation {
    HORIZONTAL,
    VERTICAL
}

data class LauncherItemState(
    val id: String = "",
    val type: LauncherItemType? = null,
    val col: Int = 0,
    val row: Int = 0,
    val colSpan: Int = 1,
    val rowSpan: Int = 1,
    val minColSpan: Int = 1,
    val minRowSpan: Int = 1,
    val widgetId: Int? = null,
    val scrollOrientation: ScrollOrientation? = ScrollOrientation.VERTICAL,
    val children: List<LauncherItemState>? = emptyList()
) {
    val safeId: String
        get() {
            val rawId = id as String?
            return if (rawId.isNullOrBlank()) "item_${UUID.randomUUID()}" else rawId
        }

    val safeType: LauncherItemType
        get() = type ?: LauncherItemType.APPS_LIST

    val safeScrollOrientation: ScrollOrientation
        get() = scrollOrientation ?: ScrollOrientation.VERTICAL

    val safeChildren: List<LauncherItemState>
        get() = children?.filterNotNull() ?: emptyList()
}
