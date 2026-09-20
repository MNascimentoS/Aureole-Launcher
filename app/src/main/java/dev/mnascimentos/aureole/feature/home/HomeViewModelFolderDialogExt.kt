package dev.mnascimentos.aureole.feature.home

import dev.mnascimentos.aureole.core.data.model.AppFolder

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
