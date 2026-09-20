package dev.mnascimentos.aureole.feature.home.ext

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
    val newFolder = AppFolder(name = name)
    folderRepository.addFolder(newFolder)
    refreshFoldersAndOpen(newFolder.id)
    updateUiState { it.copy(isCreateFolderDialogVisible = false) }
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
