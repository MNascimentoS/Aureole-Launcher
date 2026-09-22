package dev.mnascimentos.aureole.core.data.model

enum class LauncherItemType {
    CLOCK,
    APPS_LIST,
    SHORTCUTS_SIDE_PANEL,
    SINGLE_APP_WIDGET,
    WIDGET_LIST,
    SCROLL_VIEW
}

enum class ScrollOrientation {
    HORIZONTAL,
    VERTICAL
}

data class LauncherItemState(
    val id: String,
    val type: LauncherItemType? = null,
    val col: Int,
    val row: Int,
    val colSpan: Int,
    val rowSpan: Int,
    val minColSpan: Int = 1,
    val minRowSpan: Int = 1,
    val widgetId: Int? = null,
    val scrollOrientation: ScrollOrientation? = ScrollOrientation.VERTICAL,
    val children: List<LauncherItemState>? = emptyList()
) {
    val safeType: LauncherItemType
        get() = type ?: LauncherItemType.APPS_LIST

    val safeScrollOrientation: ScrollOrientation
        get() = scrollOrientation ?: ScrollOrientation.VERTICAL

    val safeChildren: List<LauncherItemState>
        get() = children ?: emptyList()
}
