package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import kotlinx.coroutines.launch

/** Deletes the folder and offers "Undo", which puts the folder back with its apps. */
internal suspend fun HomeViewModel.removeFolderWithUndo(folderId: String) {
    val folder = uiState.value.folders.find { it.id == folderId }
    deleteFolder(folderId)
    if (folder == null) return
    showUndo(TextResource.fromResId(R.string.home_undo_folder_removed)) {
        viewModelScope.launch { restoreFolder(folder) }
    }
}

private suspend fun HomeViewModel.restoreFolder(folder: AppFolder) {
    if (folderRepository.getFolders().any { it.id == folder.id }) return
    folderRepository.addFolder(folder)
    refreshFolders()
}
