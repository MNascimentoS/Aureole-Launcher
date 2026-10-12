package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState

fun handleBackNavigation(uiState: MainUiState, viewModel: HomeViewModel) {
    if (uiState.showReleaseNotesBottomSheet) {
        viewModel.dismissReleaseNotes()
    } else if (handleWidgetBackNavigation(uiState, viewModel)) {
        return
    } else if (handleFolderBackNavigation(uiState, viewModel)) {
        return
    } else if (uiState.isAllAppsDrawerOpen) {
        viewModel.setAllAppsDrawerOpen(open = false)
    } else if (uiState.searchQuery.isNotEmpty()) {
        viewModel.onSearchQueryChanged("")
    } else if (uiState.showWidgetPicker) {
        viewModel.setShowWidgetPicker(show = false)
    } else if (uiState.showFavoritePickerDialog) {
        viewModel.setShowFavoritePicker(show = false)
    } else if (uiState.showAddContainerDialog) {
        viewModel.setShowAddContainerDialog(false)
    } else if (uiState.editingGridItem != null) {
        viewModel.setEditingGridItem(null)
    } else if (uiState.isEditContainerDialogVisible) {
        viewModel.closeEditContainerDialog()
    } else if (uiState.isGridEditMode) {
        viewModel.saveGridEditMode()
    }
}

private fun handleFolderBackNavigation(uiState: MainUiState, viewModel: HomeViewModel): Boolean {
    val isFolderActive = uiState.isCreateFolderDialogVisible ||
        uiState.isAddAppToFolderDialogVisible ||
        uiState.isRenameFolderDialogVisible ||
        uiState.activeFolder != null
    if (isFolderActive) {
        viewModel.onFolderIntent(FolderViewIntent.CloseFolder)
        return true
    }
    return false
}

private fun handleWidgetBackNavigation(uiState: MainUiState, viewModel: HomeViewModel): Boolean {
    if (uiState.showWidgetPopup || uiState.showWidgetResizeDialog) {
        viewModel.closeWidgetPopup()
        return true
    }
    return false
}
