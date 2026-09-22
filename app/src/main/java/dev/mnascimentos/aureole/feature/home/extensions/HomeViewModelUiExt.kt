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
