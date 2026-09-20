package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState

fun handleBackNavigation(uiState: MainUiState, viewModel: HomeViewModel) {
    when {
        uiState.showWidgetPopup || uiState.showWidgetResizeDialog -> viewModel.closeWidgetPopup()
        uiState.isCreateFolderDialogVisible ||
        uiState.isAddAppToFolderDialogVisible ||
        uiState.isRenameFolderDialogVisible ||
        uiState.activeFolder != null -> viewModel.onFolderIntent(FolderViewIntent.CloseFolder)
        uiState.isAllAppsDrawerOpen -> viewModel.setAllAppsDrawerOpen(open = false)
        uiState.searchQuery.isNotEmpty() -> viewModel.onSearchQueryChanged("")
        uiState.showWidgetPicker -> viewModel.setShowWidgetPicker(show = false)
        uiState.showFavoritePickerDialog -> viewModel.setShowFavoritePicker(show = false)
    }
}
