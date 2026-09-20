package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

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

fun HomeViewModel.setShowFavoritePicker(show: Boolean) {
    updateUiState { it.copy(showFavoritePickerDialog = show) }
}

fun HomeViewModel.setAllAppsDrawerOpen(open: Boolean) {
    updateUiState { it.copy(isAllAppsDrawerOpen = open) }
    if (open) {
        updateUiState { it.copy(searchQuery = "") }
        applySearchFilter("")
    }
}

// --- Favorites Actions ---

fun HomeViewModel.toggleFavorite(packageName: String) {
    val currentFavs = uiState.value.favoriteAppPackages.toMutableList()
    if (currentFavs.contains(packageName)) {
        currentFavs.remove(packageName)
    } else {
        currentFavs.add(packageName)
    }
    settingsRepository.favoriteAppPackages = currentFavs
    updateUiState { it.copy(favoriteAppPackages = currentFavs) }
    updateAppsState(uiState.value.apps)
}
