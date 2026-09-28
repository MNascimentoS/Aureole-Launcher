package dev.mnascimentos.aureole.feature.home.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

fun HomeViewModel.setShowFavoritePicker(show: Boolean, containerId: String? = null) {
    updateUiState {
        it.copy(
            showFavoritePickerDialog = show,
            activeFavoriteContainerId = if (show) (containerId ?: it.activeFavoriteContainerId) else null
        )
    }
}

fun HomeViewModel.toggleFavorite(packageName: String, containerId: String? = null) {
    val targetContainerId = containerId ?: uiState.value.activeFavoriteContainerId ?: "default_favorites"
    val containerFavs = uiState.value.containerFavorites[targetContainerId] ?: uiState.value.favoriteAppPackages
    val currentFavs = containerFavs.toMutableList()
    if (currentFavs.contains(packageName)) {
        currentFavs.remove(packageName)
    } else {
        currentFavs.add(packageName)
    }
    updateContainerFavorites(targetContainerId, currentFavs)
}

fun HomeViewModel.updateFavoritePackages(newFavorites: List<String>) {
    val targetContainerId = uiState.value.activeFavoriteContainerId ?: "default_favorites"
    updateContainerFavorites(targetContainerId, newFavorites)
}

fun HomeViewModel.updateContainerFavorites(containerId: String, newFavorites: List<String>) {
    viewModelScope.launch {
        favoriteContainerRepository.updateContainerFavorites(containerId, newFavorites)
        val updatedMap = uiState.value.containerFavorites.toMutableMap()
        updatedMap[containerId] = newFavorites
        settingsRepository.favoriteAppPackages = newFavorites
        updateUiState {
            it.copy(
                favoriteAppPackages = newFavorites,
                containerFavorites = updatedMap
            )
        }
        updateAppsState(uiState.value.apps)
    }
}
