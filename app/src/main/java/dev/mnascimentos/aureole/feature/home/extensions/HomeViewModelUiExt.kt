package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent

// --- Settings & Side Panel Actions ---

fun HomeViewModel.toggleLeftHandedMode() {
    val newValue = !uiState.value.isLeftHandedMode
    settingsRepository.isLeftHandedMode = newValue
    updateUiState { it.copy(isLeftHandedMode = newValue) }
}

fun HomeViewModel.toggleContainer() {
    val newValue = !uiState.value.isContainerEnabled
    settingsRepository.isContainerEnabled = newValue
    updateUiState { it.copy(isContainerEnabled = newValue) }
}

fun HomeViewModel.setContainerPosition(position: String) {
    settingsRepository.containerPosition = position
    updateUiState { it.copy(containerPosition = position) }
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
    if (open) {
        onFolderIntent(FolderViewIntent.CloseFolder)
    }
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

fun HomeViewModel.setShowCustomizeBottomSheet(show: Boolean) {
    updateUiState { it.copy(showCustomizeBottomSheet = show) }
}
