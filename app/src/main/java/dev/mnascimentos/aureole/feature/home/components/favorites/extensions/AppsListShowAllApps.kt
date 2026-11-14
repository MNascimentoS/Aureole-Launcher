package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The apps list with [itemId] on the home or inside a scroll group, or null. */
fun List<LauncherItemState>.findAppsList(itemId: String): LauncherItemState? = firstNotNullOfOrNull { item ->
    item.takeIf { it.id == itemId && it.safeType == LauncherItemType.APPS_LIST }
        ?: item.safeChildren.findAppsList(itemId)
}

/**
 * Sets "Show all apps" to [show] on the apps list with [itemId], also inside scroll groups. Returns the same list
 * instance when nothing changes, so callers can skip the save.
 */
fun List<LauncherItemState>.withShowAllApps(itemId: String, show: Boolean): List<LauncherItemState> {
    var changed = false
    val updated = map { item ->
        val withChildren = item.children?.filterNotNull()?.let { children ->
            val newChildren = children.withShowAllApps(itemId, show)
            if (newChildren !== children) item.copy(children = newChildren) else item
        } ?: item
        val isTarget = item.id == itemId && item.safeType == LauncherItemType.APPS_LIST
        val result = if (isTarget && item.showAllApps != show) withChildren.copy(showAllApps = show) else withChildren
        if (result !== item) changed = true
        result
    }
    return if (changed) updated else this
}

/**
 * The state with "Show all apps" set on the apps list [itemId] in both orientations. In the grid edit mode the saved
 * layout the edit started from ([MainUiState.cachedPortraitGridItems]) changes too, so the switch survives a cancel
 * of the edit and saving it does not save the unfinished edit.
 */
fun MainUiState.withShowAllApps(itemId: String, show: Boolean): MainUiState = copy(
    gridItems = gridItems.withShowAllApps(itemId, show),
    portraitGridItems = portraitGridItems.withShowAllApps(itemId, show),
    landscapeGridItems = landscapeGridItems.withShowAllApps(itemId, show),
    cachedGridItems = cachedGridItems?.withShowAllApps(itemId, show),
    cachedPortraitGridItems = cachedPortraitGridItems?.withShowAllApps(itemId, show),
    cachedLandscapeGridItems = cachedLandscapeGridItems?.withShowAllApps(itemId, show)
)

/** The layout of one orientation as it is saved: the one the grid edit started from while it is open. */
fun MainUiState.savedGridItems(isLandscape: Boolean): List<LauncherItemState> = if (isLandscape) {
    cachedLandscapeGridItems ?: landscapeGridItems
} else {
    cachedPortraitGridItems ?: portraitGridItems
}
