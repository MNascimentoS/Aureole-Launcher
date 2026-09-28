package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent

fun HomeViewModel.setRenameFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isRenameFolderDialogVisible = visible) }
}

fun HomeViewModel.setAddAppToFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isAddAppToFolderDialogVisible = visible) }
}

internal suspend fun HomeViewModel.createNewFolder(name: String) {
    val targetPanelId = uiState.value.targetPanelIdForFolder
    val newFolder = AppFolder(name = name, panelId = targetPanelId)

    if (targetPanelId != null) {
        sidePanelRepository.addFolderToPanel(targetPanelId, newFolder)
    } else {
        folderRepository.addFolder(newFolder)
    }

    val updatedPanels = sidePanelRepository.getAllSidePanels().associateBy { it.id }
    val updatedFolders = folderRepository.getFolders()

    updateUiState {
        it.copy(
            sidePanels = updatedPanels,
            folders = updatedFolders,
            isCreateFolderDialogVisible = false,
            targetPanelIdForFolder = null,
        )
    }

    refreshFoldersAndOpen(newFolder.id)
}

internal suspend fun HomeViewModel.saveFolderApps(intent: FolderViewIntent.SaveFolderApps) {
    folderRepository.updateFolderApps(intent.folderId, intent.selectedPackageNames)
    refreshFoldersAndOpen(intent.folderId)
    updateUiState { it.copy(isAddAppToFolderDialogVisible = false) }
}

internal suspend fun HomeViewModel.renameFolder(intent: FolderViewIntent.RenameFolder) {
    folderRepository.updateFolderDetails(intent.folderId, intent.newName, intent.icon)
    refreshFoldersAndOpen(intent.folderId)
    updateUiState { it.copy(isRenameFolderDialogVisible = false) }
}
