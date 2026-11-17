package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.withDetails
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch

/** Creates the folder with the name and look chosen in the folder form. */
fun SettingsViewModel.createFolder(details: FolderDetails) {
    viewModelScope.launch {
        val newFolder = AppFolder(name = details.name).withDetails(details)
        folderRepository.addFolder(newFolder)
        updateUiState {
            it.copy(
                showCreateFolderDialog = false,
                shouldFinishActivity = true
            )
        }
    }
}
