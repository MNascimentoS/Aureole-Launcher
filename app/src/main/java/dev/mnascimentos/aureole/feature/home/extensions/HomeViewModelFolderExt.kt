package dev.mnascimentos.aureole.feature.home.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import kotlinx.coroutines.launch

fun HomeViewModel.onFolderIntent(intent: FolderViewIntent) {
    viewModelScope.launch {
        handleFolderIntent(intent)
    }
}

private suspend fun HomeViewModel.handleFolderIntent(intent: FolderViewIntent) {
    when (intent) {
        is FolderViewIntent.OpenCreateFolderDialog -> {
            updateUiState { it.copy(isCreateFolderDialogVisible = true) }
        }
        is FolderViewIntent.SubmitFolderName -> createNewFolder(intent.name)
        is FolderViewIntent.OpenFolder -> openFolder(intent)
        is FolderViewIntent.CloseFolder -> closeFolder()
        is FolderViewIntent.LaunchApp -> launchAppFromFolder(intent.packageName)
        is FolderViewIntent.AddAppToFolder -> {
            updateUiState { it.copy(isAddAppToFolderDialogVisible = true) }
        }
        is FolderViewIntent.SaveFolderApps -> saveFolderApps(intent)
        is FolderViewIntent.RenameFolder -> renameFolder(intent)
        is FolderViewIntent.DeleteFolder -> deleteFolder(intent.folderId)
    }
}

private fun HomeViewModel.openFolder(intent: FolderViewIntent.OpenFolder) {
    val targetFolder = uiState.value.folders.find { it.id == intent.folderId }
    val panel = targetFolder?.panelId?.let { uiState.value.sidePanels[it] }
    val isAnyGridPanel = uiState.value.sidePanels.values.any { it.isGridFolderEnabled }
    val active = targetFolder?.copy(
        displayAsGrid = isAnyGridPanel || (panel?.isGridFolderEnabled == true) || targetFolder.displayAsGrid
    ) ?: targetFolder

    updateUiState {
        it.copy(
            openedFolderId = intent.folderId,
            activeFolder = active,
            activeFolderTopYPx = intent.topYPx
        )
    }
}

private fun HomeViewModel.closeFolder() {
    updateUiState {
        it.copy(
            openedFolderId = null,
            activeFolder = null,
            isAddAppToFolderDialogVisible = false,
            isRenameFolderDialogVisible = false
        )
    }
}

private fun HomeViewModel.launchAppFromFolder(packageName: String) {
    val appInfo = uiState.value.apps.find { it.packageName == packageName }
    appInfo?.let { launchApp(it.componentName) }
    closeFolder()
}

private suspend fun HomeViewModel.deleteFolder(folderId: String) {
    folderRepository.deleteFolder(folderId)
    refreshFolders()
    closeFolder()
}

internal suspend fun HomeViewModel.refreshFoldersAndOpen(folderId: String) {
    val updatedFolders = folderRepository.getFolders()
    val active = updatedFolders.find { it.id == folderId }
    updateUiState {
        it.copy(
            folders = updatedFolders,
            openedFolderId = active?.id,
            activeFolder = active
        )
    }
}

private suspend fun HomeViewModel.refreshFolders() {
    val updatedFolders = folderRepository.getFolders()
    updateUiState { it.copy(folders = updatedFolders) }
}
