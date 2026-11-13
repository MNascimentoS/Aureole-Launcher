package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
enum class LauncherItemType {
    CLOCK,

    /** The apps list: the list's favorites on top and, with [LauncherItemState.showAllApps], every other app. */
    APPS_LIST,

    /**
     * The all apps list of 0.9.30 and older. Kept only to read layouts saved by those versions: loading turns it
     * into an [APPS_LIST] that shows all apps. No code creates it.
     */
    ALL_APPS_LIST,
    SHORTCUTS_CONTAINER,
    SINGLE_APP_WIDGET,
    WIDGET_LIST,
    SCROLL_VIEW,
    QUICK_BUTTON
}

@Keep
enum class ScrollOrientation {
    HORIZONTAL,
    VERTICAL
}

@Keep
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
    val quickButtonModel: QuickButtonModel? = null,
    /** Apps list only: whether it shows every app below its favorites. Null in memory means on (a new list). */
    val showAllApps: Boolean? = null,
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

    /** Whether this apps list shows all apps below its favorites; a new list does. */
    val isShowingAllApps: Boolean
        get() = showAllApps ?: true

    val safeChildren: List<LauncherItemState>
        get() = children?.filterNotNull() ?: emptyList()
}
