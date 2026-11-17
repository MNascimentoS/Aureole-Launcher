package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent

fun HomeViewModel.setRenameFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isRenameFolderDialogVisible = visible) }
}

fun HomeViewModel.setAddAppToFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isAddAppToFolderDialogVisible = visible) }
}

internal suspend fun HomeViewModel.createNewFolder(name: String, icon: String? = null) {
    val targetPanelId = uiState.value.targetPanelIdForFolder
    val newFolder = AppFolder(name = name, icon = icon, panelId = targetPanelId)

    if (targetPanelId != null) {
        containerRepository.addFolderToPanel(targetPanelId, newFolder)
    } else {
        folderRepository.addFolder(newFolder)
    }

    val updatedPanels = containerRepository.getAllContainers().associateBy { it.id }
    val updatedFolders = folderRepository.getFolders()

    updateUiState {
        it.copy(
            containers = updatedPanels,
            folders = updatedFolders,
            isCreateFolderDialogVisible = false,
            targetPanelIdForFolder = null,
        )
    }

    refreshFoldersAndOpen(newFolder.id)
}

/** Saves the apps of the folder and offers an "Undo" that puts the previous apps back. */
internal suspend fun HomeViewModel.saveFolderApps(intent: FolderViewIntent.SaveFolderApps) {
    val before = uiState.value.folders.find { it.id == intent.folderId }
    folderRepository.updateFolderApps(intent.folderId, intent.selectedPackageNames)
    refreshFoldersAndOpen(intent.folderId)
    updateUiState { it.copy(isAddAppToFolderDialogVisible = false) }
    if (before != null && before.appPackageNames != intent.selectedPackageNames) {
        offerFolderUndo { folderRepository.updateFolderApps(before.id, before.appPackageNames) }
    }
}

/** Saves the name and icon of the folder and offers an "Undo" that puts the previous ones back. */
internal suspend fun HomeViewModel.renameFolder(intent: FolderViewIntent.RenameFolder) {
    val before = uiState.value.folders.find { it.id == intent.folderId }
    folderRepository.updateFolderDetails(intent.folderId, intent.newName, intent.icon)
    refreshFoldersAndOpen(intent.folderId)
    updateUiState { it.copy(isRenameFolderDialogVisible = false) }
    if (before != null && (before.name != intent.newName || before.icon != intent.icon)) {
        offerFolderUndo { folderRepository.updateFolderDetails(before.id, before.name, before.icon) }
    }
}
