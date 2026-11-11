package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import kotlinx.coroutines.launch

fun HomeViewModel.onFolderIntent(intent: FolderViewIntent) {
    viewModelScope.launch {
        handleFolderIntent(intent)
    }
}

internal suspend fun HomeViewModel.handleFolderIntent(intent: FolderViewIntent) {
    when (intent) {
        is FolderViewIntent.OpenCreateFolderDialog -> {
            updateUiState {
                it.copy(
                    isCreateFolderDialogVisible = true,
                    targetPanelIdForFolder = intent.panelId,
                )
            }
        }
        is FolderViewIntent.SubmitFolderName -> createNewFolder(intent.name, intent.icon)
        is FolderViewIntent.OpenFolder -> openFolder(intent)
        is FolderViewIntent.CloseFolder -> closeFolder()
        is FolderViewIntent.LaunchApp -> launchAppFromFolder(intent.packageName)
        is FolderViewIntent.AddAppToFolder -> {
            updateUiState { it.copy(isAddAppToFolderDialogVisible = true) }
        }
        is FolderViewIntent.SaveFolderApps -> saveFolderApps(intent)
        is FolderViewIntent.RenameFolder -> renameFolder(intent)
        is FolderViewIntent.DeleteFolder -> removeFolderWithUndo(intent.folderId)
    }
}

private fun HomeViewModel.openFolder(intent: FolderViewIntent.OpenFolder) {
    val targetFolder = uiState.value.folders.find { it.id == intent.folderId }
    val panel = targetFolder?.panelId?.let { uiState.value.containers[it] }
    val isGrid = (panel?.isGridFolderEnabled == true) || (targetFolder?.displayAsGrid == true)
    val active = targetFolder?.copy(
        displayAsGrid = isGrid
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
            isRenameFolderDialogVisible = false,
            isCreateFolderDialogVisible = false,
            targetPanelIdForFolder = null
        )
    }
}

private fun HomeViewModel.launchAppFromFolder(packageName: String) {
    val appInfo = uiState.value.apps.find { it.packageName == packageName }
    appInfo?.let { launchApp(it.packageName, it.componentName.className) }
    closeFolder()
}

internal suspend fun HomeViewModel.deleteFolder(folderId: String) {
    folderRepository.deleteFolder(folderId)
    refreshFolders()
    closeFolder()
}

internal suspend fun HomeViewModel.refreshFoldersAndOpen(folderId: String) {
    val updatedFolders = folderRepository.getFolders()
    val updatedPanels = containerRepository.getAllContainers().associateBy { it.id }
    val active = updatedFolders.find { it.id == folderId }
    updateUiState {
        it.copy(
            containers = updatedPanels,
            folders = updatedFolders,
            openedFolderId = active?.id,
            activeFolder = active
        )
    }
}

internal suspend fun HomeViewModel.refreshFolders() {
    val updatedFolders = folderRepository.getFolders()
    val updatedPanels = containerRepository.getAllContainers().associateBy { it.id }
    updateUiState {
        it.copy(
            containers = updatedPanels,
            folders = updatedFolders
        )
    }
}

fun HomeViewModel.removeAppFromFolder(folderId: String, packageName: String) {
    viewModelScope.launch {
        val folder = uiState.value.folders.find { it.id == folderId } ?: return@launch
        val originalPackages = folder.appPackageNames
        val index = originalPackages.indexOf(packageName)
        val newAppPackages = originalPackages.filterNot { it == packageName }
        folderRepository.updateFolderApps(folderId, newAppPackages)
        refreshFoldersAndOpen(folderId)
        if (index < 0) return@launch
        val appName = uiState.value.apps.find { it.packageName == packageName }?.label ?: packageName
        showUndo(TextResource.fromResId(R.string.home_undo_removed_from_folder, appName)) {
            viewModelScope.launch { restoreFolderApp(folderId, packageName, index) }
        }
    }
}

private suspend fun HomeViewModel.restoreFolderApp(folderId: String, packageName: String, index: Int) {
    val current = folderRepository.getFolders().find { it.id == folderId } ?: return
    if (packageName in current.appPackageNames) return
    val restored = current.appPackageNames.toMutableList()
    restored.add(index.coerceAtMost(restored.size), packageName)
    folderRepository.updateFolderApps(folderId, restored)
    refreshFoldersAndOpen(folderId)
}
