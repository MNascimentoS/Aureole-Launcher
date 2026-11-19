package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.details
import dev.mnascimentos.aureole.core.data.model.withDetails
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.afterFolderCreated

fun HomeViewModel.setRenameFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isRenameFolderDialogVisible = visible) }
}

fun HomeViewModel.setAddAppToFolderDialogVisible(visible: Boolean) {
    updateUiState { it.copy(isAddAppToFolderDialogVisible = visible) }
}

/** Creates the folder and opens it; [pickApps] also opens its app picker ("Create and pick apps"). */
internal suspend fun HomeViewModel.createNewFolder(details: FolderDetails, pickApps: Boolean = false) {
    val targetPanelId = uiState.value.targetPanelIdForFolder
    val newFolder = AppFolder(name = details.name, panelId = targetPanelId).withDetails(details)

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
    updateUiState { it.afterFolderCreated(pickApps) }
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

/** Saves the name and look of the folder and offers an "Undo" that puts the previous ones back. */
internal suspend fun HomeViewModel.renameFolder(intent: FolderViewIntent.RenameFolder) {
    val before = uiState.value.folders.find { it.id == intent.folderId }
    folderRepository.updateFolderDetails(intent.folderId, intent.details)
    refreshFoldersAndOpen(intent.folderId)
    updateUiState { it.copy(isRenameFolderDialogVisible = false) }
    if (before != null && before.details() != intent.details) {
        offerFolderUndo { folderRepository.updateFolderDetails(before.id, before.details()) }
    }
}
