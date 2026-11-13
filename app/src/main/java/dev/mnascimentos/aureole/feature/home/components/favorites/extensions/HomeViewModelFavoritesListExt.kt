package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.placeNewGridItem
import dev.mnascimentos.aureole.feature.home.model.favoritesOf
import dev.mnascimentos.aureole.feature.home.model.isLegacyFavoritesId
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Customize › Add apps list: places a new apps list (even if the home has one) and opens its picker. The list shows
 * all apps, so it stays on the home even if the picker closes without favorites.
 */
fun HomeViewModel.addAppsList() {
    val listId = placeNewGridItem(LauncherItemType.APPS_LIST, alwaysNewId = true) ?: return
    setShowFavoritePicker(true, listId)
}

/** Closes the picker. */
fun HomeViewModel.dismissFavoritePicker() {
    setShowFavoritePicker(false)
}

/**
 * "Show all apps" of the apps list [itemId]: applies at once in both orientations, inside scroll groups too, and
 * saves the layouts it changed (in the grid edit mode, the layout the edit started from).
 */
fun HomeViewModel.setShowAllApps(itemId: String, show: Boolean) {
    val before = uiState.value
    val after = before.withShowAllApps(itemId, show)
    if (after == before) return
    updateUiState { it.withShowAllApps(itemId, show) }
    listOf(false, true).forEach { isLandscape ->
        val saved = after.savedGridItems(isLandscape)
        if (saved !== before.savedGridItems(isLandscape)) gridRepository.saveGridItems(saved, isLandscape)
    }
}

/**
 * Erases the saved favorites of the list [listId] (its row and, for the legacy list, the global favorites) and
 * returns what puts them back. The delete is immediate and the undo restores a snapshot, instead of deleting when
 * the 4 s offer expires: [dev.mnascimentos.aureole.core.undo.UndoController] has no expiry callback and a newer
 * offer silently drops the older one, so a delayed delete would leave orphan rows behind.
 */
internal fun HomeViewModel.dropFavoritesList(listId: String): () -> Unit {
    val state = uiState.value
    val previous = state.favoritesOf(listId)
    val isLegacy = isLegacyFavoritesId(listId)
    viewModelScope.launch {
        runCatching { favoriteContainerRepository.deleteContainer(listId) }.onFailure { Timber.e(it) }
        if (isLegacy) settingsRepository.favoriteAppPackages = emptyList()
        updateUiState {
            it.copy(
                favoriteAppPackages = if (isLegacy) emptyList() else it.favoriteAppPackages,
                containerFavorites = it.containerFavorites - listId
            )
        }
    }
    return { updateContainerFavorites(listId, previous) }
}
