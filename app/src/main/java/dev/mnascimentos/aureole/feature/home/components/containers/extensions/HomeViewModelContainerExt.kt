package dev.mnascimentos.aureole.feature.home.components.containers.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.hasItemOnAnyLayout
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupCloseOutcome
import dev.mnascimentos.aureole.feature.home.components.containers.model.groupCloseOutcome
import dev.mnascimentos.aureole.feature.home.components.containers.model.withEditContainerClosed
import dev.mnascimentos.aureole.feature.home.components.containers.model.withEditContainerOpened
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.confirmContainerTaskOnSave
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.extensions.containerIds
import dev.mnascimentos.aureole.feature.home.grid.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.missingContainerIds
import kotlinx.coroutines.launch

fun HomeViewModel.loadContainers() {
    viewModelScope.launch {
        val allPanels = containerRepository.getAllContainers()
        val panelsMap = allPanels.associateBy { it.id }.toMutableMap()

        val state = uiState.value
        (state.gridItems + state.portraitGridItems + state.landscapeGridItems)
            .missingContainerIds(panelsMap.keys)
            .forEach { id -> panelsMap[id] = ensureContainer(id) }

        updateUiState { it.copy(containers = panelsMap) }
    }
}

/**
 * Makes sure the container [id] exists, with the initial values: position and expand cell come from the settings
 * (they only seed a new container), and the default layout's container starts without a background.
 */
internal suspend fun HomeViewModel.ensureContainer(id: String): ContainerModel =
    containerRepository.ensureContainerExists(
        panelId = id,
        isBackgroundEnabled = id != GridDefaults.CONTAINER_ITEM_ID,
        position = settingsRepository.containerPosition,
        isExpandCell = settingsRepository.isContainerExpandCell
    )

/**
 * Erases the containers among [items] (items, folders and config) and returns what puts them back. A container
 * still on the other layout, or any removal during grid edit (which can be cancelled), is left alone. Same
 * immediate delete + snapshot undo as `dropFavoritesList`.
 */
internal fun HomeViewModel.dropContainers(items: List<LauncherItemState>): () -> Unit {
    val state = uiState.value
    val snapshots = if (state.isGridEditMode) {
        emptyList()
    } else {
        items.containerIds().filterNot { state.hasItemOnAnyLayout(it) }.mapNotNull { state.containers[it] }
    }
    if (snapshots.isNotEmpty()) {
        viewModelScope.launch {
            snapshots.forEach { containerRepository.deleteContainer(it.id) }
            refreshContainers()
        }
    }
    return {
        if (snapshots.isNotEmpty()) {
            viewModelScope.launch {
                snapshots.forEach { containerRepository.saveContainer(it) }
                refreshContainers()
            }
        }
    }
}

/** Puts a container just created in the database into the state, so it renders (empty) at once. */
internal fun HomeViewModel.refreshContainerInState(model: ContainerModel) {
    updateUiState { it.copy(containers = it.containers + (model.id to model)) }
}

private suspend fun HomeViewModel.refreshContainers() {
    val containers = containerRepository.getAllContainers().associateBy { it.id }
    val folders = folderRepository.getFolders()
    updateUiState { it.copy(containers = containers, folders = folders) }
}

fun HomeViewModel.openEditContainerDialog(panelId: String) {
    viewModelScope.launch {
        val panel = containerRepository.getContainer(panelId) ?: ensureContainer(panelId)
        val currentMap = uiState.value.containers.toMutableMap()
        currentMap[panelId] = panel

        updateUiState { it.withEditContainerOpened(panelId).copy(containers = currentMap) }
    }
}

/** Closes the editor; with a [panelId] it only closes if that container is still the one being edited. */
fun HomeViewModel.closeEditContainerDialog(panelId: String? = null) {
    updateUiState { it.withEditContainerClosed(panelId) }
}

/**
 * Saves the group closed in the editor, or any change to it, and offers what fits (RF-B7, RF-B8): "Group updated"
 * with an Undo that puts back how it was, "Group added to the home" with an Undo that removes a group just added, or,
 * for a group just added and closed empty, takes it off the grid with no snackbar (D-11).
 */
fun HomeViewModel.saveContainerModel(panel: ContainerModel) {
    val state = uiState.value
    val isNew = state.editingContainerIsNew && state.editingContainerId == panel.id
    val outcome = groupCloseOutcome(previous = state.containers[panel.id], saved = panel, isNew = isNew)
    if (outcome == GroupCloseOutcome.DiscardedEmpty) {
        discardContainer(panel.id)
        return
    }
    confirmContainerTaskOnSave()
    viewModelScope.launch {
        containerRepository.saveContainer(panel)
        refreshContainersClosing(panel.id)
        when (outcome) {
            is GroupCloseOutcome.Updated -> showUndo(TextResource.fromResId(R.string.home_container_saved)) {
                viewModelScope.launch {
                    containerRepository.saveContainer(outcome.previous)
                    val restoredPanels = containerRepository.getAllContainers().associateBy { it.id }
                    updateUiState { current -> current.copy(containers = restoredPanels) }
                }
            }
            GroupCloseOutcome.Added -> showUndo(TextResource.fromResId(R.string.home_container_added)) {
                discardContainer(panel.id)
            }
            else -> Unit
        }
    }
}

/** Takes the group [panelId] off the grid and erases it (a new group closed empty, or the Undo of "added"). */
private fun HomeViewModel.discardContainer(panelId: String) {
    deleteGridItem(panelId)
    viewModelScope.launch {
        containerRepository.deleteContainer(panelId)
        refreshContainersClosing(panelId)
    }
}

private suspend fun HomeViewModel.refreshContainersClosing(panelId: String) {
    val updatedPanels = containerRepository.getAllContainers().associateBy { it.id }
    val updatedFolders = folderRepository.getFolders()
    updateUiState {
        it.copy(containers = updatedPanels, folders = updatedFolders).withEditContainerClosed(panelId)
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
