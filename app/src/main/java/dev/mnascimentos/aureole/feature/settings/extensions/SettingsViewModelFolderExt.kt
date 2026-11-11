package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch

fun SettingsViewModel.createFolder(name: String) {
    viewModelScope.launch {
        val newFolder = AppFolder(name = name)
        folderRepository.addFolder(newFolder)
        updateUiState {
            it.copy(
                showCreateFolderDialog = false,
                shouldFinishActivity = true
            )
        }
    }
}
