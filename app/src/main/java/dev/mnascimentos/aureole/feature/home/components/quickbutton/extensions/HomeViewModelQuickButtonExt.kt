package dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

fun HomeViewModel.onOpenQuickButtonSettings(id: String) {
    val state = uiState.value
    val gridItem = QuickButtonUtils.findItemById(state.gridItems, id)
    val model = gridItem?.quickButtonModel
        ?: QuickButtonUtils.findQuickButtonInContainers(state.containers.values, id)
        ?: if (gridItem != null) QuickButtonModel(id = id) else return

    updateUiState {
        it.copy(
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

    val updatedContainers = QuickButtonUtils.updateQuickButtonInContainers(state.containers, activeId, activeModel)
    if (updatedContainers != state.containers) {
        viewModelScope.launch {
            updatedContainers.values.forEach { container ->
                if (state.containers[container.id] != container) {
                    containerRepository.saveContainer(container)
                }
            }
        }
    }

    updateUiState {
        it.copy(
            gridItems = updatedCurrent,
            portraitGridItems = updatedPortrait,
            landscapeGridItems = updatedLandscape,
            containers = updatedContainers
        )
    }
}

internal object QuickButtonUtils {
    fun findItemById(items: List<LauncherItemState>, id: String): LauncherItemState? {
        return items.firstNotNullOfOrNull { item ->
            if (item.id == id) item else findItemById(item.safeChildren, id)
        }
    }

    private fun isMatchingQuickButton(item: ContainerItemEntity, id: String): Boolean {
        if (item.itemType != ContainerItemType.QUICK_BUTTON) return false
        return item.quickButtonId == id || item.id == id || item.quickButtonModel?.id == id
    }

    fun findQuickButtonInContainers(containers: Collection<ContainerModel>, id: String): QuickButtonModel? {
        return containers.firstNotNullOfOrNull { container ->
            container.quickButtons.find { it.id == id } ?: container.items.firstNotNullOfOrNull { item ->
                if (isMatchingQuickButton(item, id)) {
                    item.quickButtonModel ?: item.quickButtonId?.let { qbId -> QuickButtonModel(id = qbId) }
                } else {
                    null
                }
            }
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

    fun updateQuickButtonInContainers(
        containers: Map<String, ContainerModel>,
        activeId: String,
        activeModel: QuickButtonModel
    ): Map<String, ContainerModel> {
        val result = containers.toMutableMap()
        for ((panelId, container) in containers) {
            val hasInQuickButtons = container.quickButtons.any { it.id == activeId }
            val hasInItems = container.items.any { item -> isMatchingQuickButton(item, activeId) }

            if (hasInQuickButtons || hasInItems) {
                val updatedQuickButtons = container.quickButtons.map { qb ->
                    if (qb.id == activeId) activeModel else qb
                }.let { list ->
                    if (list.none { it.id == activeId }) list + activeModel else list
                }

                val updatedItems = container.items.map { item ->
                    if (isMatchingQuickButton(item, activeId)) {
                        item.copy(
                            quickButtonId = activeModel.id
                        ).also { it.quickButtonModel = activeModel }
                    } else {
                        item
                    }
                }

                result[panelId] = container.copy(
                    quickButtons = updatedQuickButtons,
                    items = updatedItems
                )
            }
        }
        return result
    }
}
