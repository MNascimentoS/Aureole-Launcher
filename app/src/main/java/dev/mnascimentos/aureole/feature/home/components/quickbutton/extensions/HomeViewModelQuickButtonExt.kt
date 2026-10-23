package dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.onOpenQuickButtonSettings(id: String) {
    updateUiState { state ->
        val existingItem = QuickButtonUtils.findItemById(state.gridItems, id) ?: return@updateUiState state
        val model = existingItem.quickButtonModel ?: QuickButtonModel(id = id)
        state.copy(
            showQuickButtonSettingsBottomSheet = true,
            activeQuickButtonId = id,
            activeQuickButtonModel = model
        )
    }
}

fun HomeViewModel.onCloseQuickButtonSettings() {
    updateUiState { state ->
        state.copy(
            showQuickButtonSettingsBottomSheet = false,
            activeQuickButtonId = null,
            activeQuickButtonModel = null
        )
    }
}

fun HomeViewModel.onOpenQuickButtonAppearance() {
    updateUiState { state ->
        state.copy(
            showQuickButtonSettingsBottomSheet = false,
            showQuickButtonAppearanceBottomSheet = true
        )
    }
}

fun HomeViewModel.onCloseQuickButtonAppearance() {
    updateUiState { state ->
        state.copy(
            showQuickButtonAppearanceBottomSheet = false,
            showQuickButtonSettingsBottomSheet = true
        )
    }
}

fun HomeViewModel.onOpenShortcutSelection(type: String) {
    updateUiState { state ->
        state.copy(
            showQuickButtonSettingsBottomSheet = false,
            showShortcutSelectionBottomSheet = true,
            editingShortcutType = type
        )
    }
}

fun HomeViewModel.onCloseShortcutSelection() {
    updateUiState { state ->
        state.copy(
            showShortcutSelectionBottomSheet = false,
            editingShortcutType = null,
            showQuickButtonSettingsBottomSheet = true
        )
    }
}

fun HomeViewModel.onUpdateQuickButtonAppearance(appearance: QuickButtonAppearance) {
    updateUiState { state ->
        val currentModel = state.activeQuickButtonModel ?: return@updateUiState state
        val updatedModel = currentModel.copy(appearance = appearance)
        state.copy(activeQuickButtonModel = updatedModel)
    }
    saveActiveQuickButtonModel()
}

fun HomeViewModel.onUpdateQuickButtonShortcuts(shortcuts: QuickButtonShortcuts) {
    updateUiState { state ->
        val currentModel = state.activeQuickButtonModel ?: return@updateUiState state
        val updatedModel = currentModel.copy(shortcuts = shortcuts)
        state.copy(activeQuickButtonModel = updatedModel)
    }
    saveActiveQuickButtonModel()
}

private fun HomeViewModel.saveActiveQuickButtonModel() {
    val state = uiState.value
    val activeId = state.activeQuickButtonId ?: return
    val activeModel = state.activeQuickButtonModel ?: return

    val updatedPortrait = QuickButtonUtils.updateQuickButtonInList(state.portraitGridItems, activeId, activeModel)
    val updatedLandscape = QuickButtonUtils.updateQuickButtonInList(state.landscapeGridItems, activeId, activeModel)
    val updatedCurrent = if (state.isLandscape) updatedLandscape else updatedPortrait

    gridRepository.saveGridItems(updatedPortrait, isLandscape = false)
    gridRepository.saveGridItems(updatedLandscape, isLandscape = true)

    updateUiState {
        it.copy(
            gridItems = updatedCurrent,
            portraitGridItems = updatedPortrait,
            landscapeGridItems = updatedLandscape
        )
    }
}

private object QuickButtonUtils {
    fun findItemById(items: List<LauncherItemState>, id: String): LauncherItemState? {
        return items.firstNotNullOfOrNull { item ->
            if (item.id == id) item else findItemById(item.safeChildren, id)
        }
    }

    fun updateQuickButtonInList(
        items: List<LauncherItemState>,
        activeId: String,
        activeModel: QuickButtonModel
    ): List<LauncherItemState> {
        return items.map { item ->
            if (item.id == activeId) {
                item.copy(quickButtonModel = activeModel)
            } else if (item.children != null) {
                item.copy(children = updateQuickButtonInList(item.safeChildren, activeId, activeModel))
            } else {
                item
            }
        }
    }
}
