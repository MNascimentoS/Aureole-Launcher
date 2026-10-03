package dev.mnascimentos.aureole.feature.home.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

fun HomeViewModel.loadContainers() {
    viewModelScope.launch {
        val allPanels = containerRepository.getAllContainers()
        val panelsMap = allPanels.associateBy { it.id }.toMutableMap()

        // Garante que todo item de painel no grid tenha uma entrada no banco
        uiState.value.gridItems.filter { it.safeType == LauncherItemType.SHORTCUTS_CONTAINER }
            .forEach { item ->
                if (!panelsMap.containsKey(item.id)) {
                    val ensured = containerRepository.ensureContainerExists(item.id)
                    panelsMap[item.id] = ensured
                }
            }

        updateUiState { it.copy(containers = panelsMap) }
    }
}

fun HomeViewModel.openEditContainerDialog(panelId: String) {
    viewModelScope.launch {
        val panel = containerRepository.getContainer(panelId)
            ?: containerRepository.ensureContainerExists(panelId)
        val currentMap = uiState.value.containers.toMutableMap()
        currentMap[panelId] = panel

        updateUiState {
            it.copy(
                containers = currentMap,
                editingContainerId = panelId,
                isEditContainerDialogVisible = true
            )
        }
    }
}

fun HomeViewModel.closeEditContainerDialog() {
    updateUiState {
        it.copy(
            isEditContainerDialogVisible = false,
            editingContainerId = null
        )
    }
}

fun HomeViewModel.saveContainerModel(panel: ContainerModel) {
    viewModelScope.launch {
        containerRepository.saveContainer(panel)
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
