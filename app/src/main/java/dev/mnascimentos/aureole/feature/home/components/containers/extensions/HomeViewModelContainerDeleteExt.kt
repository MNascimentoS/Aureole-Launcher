package dev.mnascimentos.aureole.feature.home.components.containers.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

fun HomeViewModel.deleteContainerInstance(panelId: String) {
    viewModelScope.launch {
        containerRepository.deleteContainer(panelId)
        val updatedPanels = containerRepository.getAllContainers().associateBy { it.id }
        val updatedFolders = folderRepository.getFolders()

        updateUiState {
            it.copy(
                containers = updatedPanels,
                folders = updatedFolders,
                isEditContainerDialogVisible = false,
                editingContainerId = null
            )
        }
    }
}

fun HomeViewModel.removeAppFromContainer(panelId: String, packageName: String) {
    val currentContainer = uiState.value.containers[panelId] ?: return
    val updatedItems = currentContainer.items.filterNot { it.packageName == packageName }
    val updatedAppPackages = currentContainer.appPackageNames.filterNot { it == packageName }
    val updatedContainer = currentContainer.copy(
        items = updatedItems,
        appPackageNames = updatedAppPackages
    )
    saveContainerModel(updatedContainer)
}
