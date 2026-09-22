package dev.mnascimentos.aureole.feature.home.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.SidePanelModel
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

fun HomeViewModel.loadSidePanels() {
    viewModelScope.launch {
        val allPanels = sidePanelRepository.getAllSidePanels()
        val panelsMap = allPanels.associateBy { it.id }.toMutableMap()

        // Garante que todo item de painel no grid tenha uma entrada no banco
        uiState.value.gridItems.filter { it.safeType == LauncherItemType.SHORTCUTS_SIDE_PANEL }
            .forEach { item ->
                if (!panelsMap.containsKey(item.id)) {
                    val ensured = sidePanelRepository.ensureSidePanelExists(item.id)
                    panelsMap[item.id] = ensured
                }
            }

        updateUiState { it.copy(sidePanels = panelsMap) }
    }
}

fun HomeViewModel.openEditSidePanelDialog(panelId: String) {
    viewModelScope.launch {
        val panel = sidePanelRepository.getSidePanel(panelId)
            ?: sidePanelRepository.ensureSidePanelExists(panelId)
        val currentMap = uiState.value.sidePanels.toMutableMap()
        currentMap[panelId] = panel

        updateUiState {
            it.copy(
                sidePanels = currentMap,
                editingSidePanelId = panelId,
                isEditSidePanelDialogVisible = true
            )
        }
    }
}

fun HomeViewModel.closeEditSidePanelDialog() {
    updateUiState {
        it.copy(
            isEditSidePanelDialogVisible = false,
            editingSidePanelId = null
        )
    }
}

fun HomeViewModel.saveSidePanelModel(panel: SidePanelModel) {
    viewModelScope.launch {
        sidePanelRepository.saveSidePanel(panel)
        val updatedPanels = sidePanelRepository.getAllSidePanels().associateBy { it.id }
        val updatedFolders = folderRepository.getFolders()

        updateUiState {
            it.copy(
                sidePanels = updatedPanels,
                folders = updatedFolders,
                isEditSidePanelDialogVisible = false,
                editingSidePanelId = null
            )
        }
    }
}

fun HomeViewModel.deleteSidePanelInstance(panelId: String) {
    viewModelScope.launch {
        sidePanelRepository.deleteSidePanel(panelId)
        val updatedPanels = sidePanelRepository.getAllSidePanels().associateBy { it.id }
        val updatedFolders = folderRepository.getFolders()

        updateUiState {
            it.copy(
                sidePanels = updatedPanels,
                folders = updatedFolders,
                isEditSidePanelDialogVisible = false,
                editingSidePanelId = null
            )
        }
    }
}
