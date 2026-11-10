package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.closeEditContainerDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.onFolderIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onOnboardingBack
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.closeWidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.dismissReleaseNotes
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.setEditingGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.setShowAddContainerDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.isHomeOverlayActive

fun handleBackNavigation(uiState: MainUiState, viewModel: HomeViewModel) {
    // Editors opened from the checklist sit above it, so they close first.
    if (!uiState.isHomeOverlayActive && viewModel.onOnboardingBack()) return
    handleHomeBackNavigation(uiState, viewModel)
}

private fun handleHomeBackNavigation(uiState: MainUiState, viewModel: HomeViewModel) {
    if (uiState.showReleaseNotesBottomSheet) {
        viewModel.dismissReleaseNotes()
    } else if (uiState.showWidgetPicker) {
        viewModel.setShowWidgetPicker(show = false)
    } else if (handleWidgetBackNavigation(uiState, viewModel)) {
        return
    } else if (handleFolderBackNavigation(uiState, viewModel)) {
        return
    } else if (uiState.isAllAppsDrawerOpen) {
        viewModel.setAllAppsDrawerOpen(open = false)
    } else if (uiState.searchQuery.isNotEmpty()) {
        viewModel.onSearchQueryChanged("")
    } else if (uiState.activeWidgetStackBottomSheet != null) {
        viewModel.closeWidgetStackBottomSheet()
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
