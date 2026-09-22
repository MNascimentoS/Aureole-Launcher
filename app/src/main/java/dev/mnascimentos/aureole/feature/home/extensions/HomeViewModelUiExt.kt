package dev.mnascimentos.aureole.feature.home.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

// --- Settings & Side Panel Actions ---

fun HomeViewModel.toggleLeftHandedMode() {
    val newValue = !uiState.value.isLeftHandedMode
    settingsRepository.isLeftHandedMode = newValue
    updateUiState { it.copy(isLeftHandedMode = newValue) }
}

fun HomeViewModel.toggleSidePanel() {
    val newValue = !uiState.value.isSidePanelEnabled
    settingsRepository.isSidePanelEnabled = newValue
    updateUiState { it.copy(isSidePanelEnabled = newValue) }
}

fun HomeViewModel.setSidePanelPosition(position: String) {
    settingsRepository.sidePanelPosition = position
    updateUiState { it.copy(sidePanelPosition = position) }
}

fun HomeViewModel.toggleHomeOpensAllApps() {
    val newValue = !uiState.value.homeButtonOpensAllApps
    settingsRepository.homeButtonOpensAllApps = newValue
    updateUiState { it.copy(homeButtonOpensAllApps = newValue) }
}

fun HomeViewModel.toggleShowAllAppsOnHome() {
    val newValue = !uiState.value.showAllAppsOnHome
    settingsRepository.showAllAppsOnHome = newValue
    updateUiState { it.copy(showAllAppsOnHome = newValue) }
}

fun HomeViewModel.setShowSettingsDialog(show: Boolean) {
    updateUiState { it.copy(showSettingsDialog = show) }
}

fun HomeViewModel.setShowFavoritePicker(show: Boolean, containerId: String? = null) {
    updateUiState {
        it.copy(
            showFavoritePickerDialog = show,
            activeFavoriteContainerId = if (show) (containerId ?: it.activeFavoriteContainerId) else null
        )
    }
}

fun HomeViewModel.setAllAppsDrawerOpen(open: Boolean, fromHomeButton: Boolean = false) {
    updateUiState {
        it.copy(
            isAllAppsDrawerOpen = open,
            isAllAppsOpenedFromBottom = if (open) fromHomeButton else it.isAllAppsOpenedFromBottom,
        )
    }
    if (open) {
        updateUiState { it.copy(searchQuery = "") }
        applySearchFilter("")
    }
}

// --- Favorites Actions ---

fun HomeViewModel.toggleFavorite(packageName: String, containerId: String? = null) {
    val targetContainerId = containerId ?: uiState.value.activeFavoriteContainerId ?: "default_favorites"
    val currentFavs = (uiState.value.containerFavorites[targetContainerId] ?: uiState.value.favoriteAppPackages).toMutableList()
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
