package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.ensureGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.deleteGridItem

/** Customize › Add favorites: places a favorites list (or reuses the one on the home) and opens its picker. */
fun HomeViewModel.addFavoritesList() {
    val existed = uiState.value.gridItems.any { it.safeType == LauncherItemType.APPS_LIST }
    val listId = ensureGridItem(LauncherItemType.APPS_LIST) ?: return
    updateUiState { it.copy(customizeFavoritesListId = listId.takeUnless { existed }) }
    setShowFavoritePicker(true, listId)
}

/** Closes the picker; a list Customize created and left without favorites leaves the home again. */
fun HomeViewModel.dismissFavoritePicker() {
    val createdId = uiState.value.customizeFavoritesListId
    setShowFavoritePicker(false)
    if (createdId == null) return
    val state = uiState.value
    val isEmpty = (state.containerFavorites[createdId] ?: state.favoriteAppPackages).isEmpty()
    updateUiState { it.copy(customizeFavoritesListId = null) }
    if (isEmpty) deleteGridItem(createdId)
}
