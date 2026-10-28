package dev.mnascimentos.aureole.feature.home.components.containers.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.confirmContainerTaskOnSave
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import kotlinx.coroutines.launch

fun HomeViewModel.loadContainers() {
    viewModelScope.launch {
        val allPanels = containerRepository.getAllContainers()
        val panelsMap = allPanels.associateBy { it.id }.toMutableMap()

        val state = uiState.value
        (state.gridItems + state.portraitGridItems + state.landscapeGridItems)
            .filter { it.safeType == LauncherItemType.SHORTCUTS_CONTAINER }
            .distinctBy { it.id }
            .forEach { item ->
                if (!panelsMap.containsKey(item.id)) {
                    // The default layout's container starts without a background.
                    val ensured = containerRepository.ensureContainerExists(
                        panelId = item.id,
                        isBackgroundEnabled = item.id != GridDefaults.CONTAINER_ITEM_ID
                    )
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
    confirmContainerTaskOnSave()
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
