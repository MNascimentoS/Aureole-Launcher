package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The "Favorites" and "All apps" labels show only on a list that shows all apps, and only when not turned off. */
fun shouldShowLabels(showAllApps: Boolean, showLabels: Boolean): Boolean = showAllApps && showLabels

/**
 * Sets the labels switch to [show] on the apps list with [itemId], also inside scroll groups. Returns the same list
 * instance when nothing changes, so callers can skip the save.
 */
fun List<LauncherItemState>.withShowLabels(itemId: String, show: Boolean): List<LauncherItemState> {
    var changed = false
    val updated = map { item ->
        val withChildren = item.children?.filterNotNull()?.let { children ->
            val newChildren = children.withShowLabels(itemId, show)
            if (newChildren !== children) item.copy(children = newChildren) else item
        } ?: item
        val isTarget = item.id == itemId && item.safeType == LauncherItemType.APPS_LIST
        val result = if (isTarget && item.isShowingLabels != show) {
            withChildren.copy(
                showLabels = show
            )
        } else {
            withChildren
        }
        if (result !== item) changed = true
        result
    }
    return if (changed) updated else this
}

/** The state with the labels switch set on the apps list [itemId], in both orientations and the edit's saved layout. */
fun MainUiState.withShowLabels(itemId: String, show: Boolean): MainUiState = copy(
    gridItems = gridItems.withShowLabels(itemId, show),
    portraitGridItems = portraitGridItems.withShowLabels(itemId, show),
    landscapeGridItems = landscapeGridItems.withShowLabels(itemId, show),
    cachedGridItems = cachedGridItems?.withShowLabels(itemId, show),
    cachedPortraitGridItems = cachedPortraitGridItems?.withShowLabels(itemId, show),
    cachedLandscapeGridItems = cachedLandscapeGridItems?.withShowLabels(itemId, show)
)

/** Labels switch of the apps list [itemId]: applies at once in both orientations and saves the layouts it changed. */
fun HomeViewModel.setShowLabels(itemId: String, show: Boolean) {
    val before = uiState.value
    val after = before.withShowLabels(itemId, show)
    if (after == before) return
    updateUiState { it.withShowLabels(itemId, show) }
    listOf(false, true).forEach { isLandscape ->
        val saved = after.savedGridItems(isLandscape)
        if (saved !== before.savedGridItems(isLandscape)) gridRepository.saveGridItems(saved, isLandscape)
    }
}
