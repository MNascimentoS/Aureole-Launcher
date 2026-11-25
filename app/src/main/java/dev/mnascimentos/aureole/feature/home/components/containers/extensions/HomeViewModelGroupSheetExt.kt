package dev.mnascimentos.aureole.feature.home.components.containers.extensions

import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.isContainerTipsSeen
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.model.withContainerSize
import dev.mnascimentos.aureole.feature.home.components.containers.model.withEditContainerOpened
import kotlinx.coroutines.launch

/**
 * Opens "Customize group" right away on the group [panelId] just added from "Add component" (RF-A4): closing it
 * empty takes it off the grid (RF-B8), closing it with items says it was added (RF-B7).
 */
fun HomeViewModel.openNewContainerEditor(panelId: String) {
    viewModelScope.launch {
        val panel = ensureContainer(panelId)
        updateUiState {
            it.withEditContainerOpened(panelId, isNew = true).copy(containers = it.containers + (panelId to panel))
        }
    }
}

/** The first group's tips were skipped or finished (RF-F1): they, and "Start here", never show again. */
fun HomeViewModel.markContainerTipsSeen() {
    settingsRepository.isContainerTipsSeen = true
    updateUiState { it.copy(isContainerTipsSeen = true) }
}

/** Keeps in memory the size the group [panelId] has on the home, for the editor preview (RF-B3). */
fun HomeViewModel.recordContainerSize(panelId: String, size: DpSize) {
    if (uiState.value.containerSizes[panelId] == size) return
    updateUiState { it.withContainerSize(panelId, size) }
}
