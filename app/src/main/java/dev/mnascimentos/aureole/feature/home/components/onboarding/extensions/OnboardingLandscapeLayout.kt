package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

/** The three columns of the landscape home, left to right (mirrored for left-handed). */
private enum class LandscapeColumn { LEFT, CENTER, RIGHT }

private fun LauncherItemType.column(): LandscapeColumn? = when (this) {
    LauncherItemType.CLOCK, LauncherItemType.APPS_LIST -> LandscapeColumn.LEFT
    LauncherItemType.WIDGET_LIST -> LandscapeColumn.CENTER
    LauncherItemType.SHORTCUTS_CONTAINER -> LandscapeColumn.RIGHT
    else -> null
}

/** The column that takes over the width of the absent [this] one. */
private fun LandscapeColumn.heirAmong(present: Set<LandscapeColumn>): LandscapeColumn {
    val order = when (this) {
        LandscapeColumn.LEFT -> listOf(LandscapeColumn.CENTER, LandscapeColumn.RIGHT)
        LandscapeColumn.CENTER -> listOf(LandscapeColumn.RIGHT, LandscapeColumn.LEFT)
        LandscapeColumn.RIGHT -> listOf(LandscapeColumn.CENTER, LandscapeColumn.LEFT)
    }
    return order.first { it in present }
}

private fun defaultSlot(type: LauncherItemType): LauncherItemState =
    checkNotNull(GridDefaults.getDefaultItem(type, isLandscape = true, isLeftHanded = false))

/**
 * The landscape home saved when the onboarding ends, built from what the user has in portrait: only the clock,
 * apps list, widgets and container they built come along, each with its id (so favorites, clock config and
 * container items are shared), its widget stack and settings. They sit where the default landscape layout puts
 * them; the room of a missing one goes to its neighbor (without a clock the apps list takes the whole left column,
 * without widgets the container fills the center). The apps list shows all apps. With none of them in portrait,
 * the result is the default landscape layout. Mirrored for left-handed.
 */
internal fun onboardingLandscapeLayout(
    portraitItems: List<LauncherItemState>,
    isLeftHanded: Boolean
): List<LauncherItemState> {
    val built = listOf(
        LauncherItemType.CLOCK,
        LauncherItemType.APPS_LIST,
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SHORTCUTS_CONTAINER
    ).mapNotNull { type -> portraitItems.firstOrNull { it.safeType == type } }
    if (built.isEmpty()) return GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = isLeftHanded)

    val present = built.mapNotNull { it.safeType.column() }.toSet()
    val widths = LandscapeColumn.entries.associateWith { column -> columnWidth(column) }.toMutableMap()
    LandscapeColumn.entries.filter { it !in present }.forEach { absent ->
        val heir = absent.heirAmong(present)
        widths[heir] = checkNotNull(widths[heir]) + checkNotNull(widths[absent])
    }
    var nextCol = 0
    val starts = LandscapeColumn.entries.filter { it in present }.associateWith { column ->
        nextCol.also { nextCol += checkNotNull(widths[column]) }
    }
    val hasClock = built.any { it.safeType == LauncherItemType.CLOCK }
    val hasApps = built.any { it.safeType == LauncherItemType.APPS_LIST }
    return built.map { item ->
        val type = item.safeType
        val slot = defaultSlot(type)
        val column = checkNotNull(type.column())
        val sharesLeftColumn = hasClock && hasApps
        val isFullHeight = column != LandscapeColumn.LEFT || !sharesLeftColumn
        val col = checkNotNull(starts[column])
        val colSpan = checkNotNull(widths[column])
        item.copy(
            col = if (isLeftHanded) GridEngineUtils.LANDSCAPE_MAX_COLS - col - colSpan else col,
            row = if (isFullHeight) 0 else slot.row,
            colSpan = colSpan,
            rowSpan = if (isFullHeight) GridEngineUtils.LANDSCAPE_MAX_ROWS else slot.rowSpan,
            minColSpan = slot.minColSpan,
            minRowSpan = slot.minRowSpan,
            showAllApps = if (type == LauncherItemType.APPS_LIST) true else item.showAllApps
        )
    }
}

private fun columnWidth(column: LandscapeColumn): Int = when (column) {
    LandscapeColumn.LEFT -> defaultSlot(LauncherItemType.CLOCK).colSpan
    LandscapeColumn.CENTER -> defaultSlot(LauncherItemType.WIDGET_LIST).colSpan
    LandscapeColumn.RIGHT -> defaultSlot(LauncherItemType.SHORTCUTS_CONTAINER).colSpan
}
