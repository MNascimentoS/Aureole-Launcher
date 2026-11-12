package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.placeNewGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.model.favoritesOf
import dev.mnascimentos.aureole.feature.home.model.isLegacyFavoritesId
import kotlinx.coroutines.launch
import timber.log.Timber

/** Customize › Add favorites: places a new, empty favorites list (even if the home has one) and opens its picker. */
fun HomeViewModel.addFavoritesList() {
    val listId = placeNewGridItem(LauncherItemType.APPS_LIST, alwaysNewId = true) ?: return
    updateUiState { it.copy(customizeFavoritesListId = listId) }
    setShowFavoritePicker(true, listId)
}

/** Closes the picker; a list Customize created and left without favorites leaves the home again. */
fun HomeViewModel.dismissFavoritePicker() {
    val createdId = uiState.value.customizeFavoritesListId
    setShowFavoritePicker(false)
    if (createdId == null) return
    val isEmpty = uiState.value.favoritesOf(createdId).isEmpty()
    updateUiState { it.copy(customizeFavoritesListId = null) }
    if (isEmpty) deleteGridItem(createdId)
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
