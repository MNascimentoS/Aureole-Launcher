package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.SlotSearchRequest
import java.util.UUID

private const val CONTAINER_ERROR_MSG = "Não há espaço livre suficiente na grade para este container."

data class GridItemSpec(
    val type: LauncherItemType,
    val widgetId: Int? = null,
    val targetColSpan: Int? = null,
    val targetRowSpan: Int? = null,
    val minColSpan: Int? = null,
    val minRowSpan: Int? = null,
    val scrollOrientation: ScrollOrientation? = null
)

fun HomeViewModel.setShowAddContainerDialog(show: Boolean) {
    updateUiState {
        it.copy(
            showAddContainerDialog = show,
            targetParentContainerId = if (show || it.isAddingSingleWidget) it.targetParentContainerId else null
        )
    }
}

fun HomeViewModel.setEditingGridItem(item: LauncherItemState?) {
    updateUiState { it.copy(editingGridItem = item) }
}

fun HomeViewModel.addGridItem(spec: GridItemSpec) {
    val currentParentId = uiState.value.targetParentContainerId
    if (currentParentId != null) {
        addChildToScrollView(currentParentId, spec.type, spec.widgetId)
        return
    }

    updateUiState { state ->
        val currentItems = state.gridItems
        val (defaultSpans, defaultMinSpans) = GridDefaults.getDefaultSpanForType(spec.type)

        val reqTargetColSpan = spec.targetColSpan ?: defaultSpans.first
        val reqTargetRowSpan = spec.targetRowSpan ?: defaultSpans.second
        val reqMinColSpan = spec.minColSpan ?: defaultMinSpans.first
        val reqMinRowSpan = spec.minRowSpan ?: defaultMinSpans.second

        val largestSlotResult = GridEngineUtils.findLargestAvailableSlot(
            request = SlotSearchRequest(
                targetColSpan = reqTargetColSpan,
                targetRowSpan = reqTargetRowSpan,
                minColSpan = reqMinColSpan,
                minRowSpan = reqMinRowSpan
            ),
            items = currentItems
        )

        if (largestSlotResult == null) {
            state.copy(gridErrorMessage = CONTAINER_ERROR_MSG)
        } else {
            val (actualColSpan, actualRowSpan, slot) = largestSlotResult
            val newItem = LauncherItemState(
                id = UUID.randomUUID().toString(),
                type = spec.type,
                col = slot.first,
                row = slot.second,
                colSpan = actualColSpan,
                rowSpan = actualRowSpan,
                minColSpan = reqMinColSpan,
                minRowSpan = reqMinRowSpan,
                widgetId = spec.widgetId,
                scrollOrientation = spec.scrollOrientation
            )
            val updatedList = currentItems + newItem
            val wasInEditMode = state.isGridEditMode
            val newStateWithGrid = updateActiveGridItemsInState(state, updatedList).copy(
                showAddContainerDialog = false,
                targetParentContainerId = null,
                isGridEditMode = true
            )

            if (!wasInEditMode) {
                newStateWithGrid.copy(
                    cachedPortraitGridItems = state.portraitGridItems,
                    cachedLandscapeGridItems = state.landscapeGridItems,
                    cachedGridItems = state.gridItems
                )
            } else {
                newStateWithGrid
            }
        }
    }
}

fun HomeViewModel.deleteGridItem(id: String) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val updatedList = currentItems.filterNot { it.id == id }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedList, state.isLandscape)
        }
        updateActiveGridItemsInState(state, updatedList).copy(editingGridItem = null)
    }
}

fun HomeViewModel.dismissGridError() {
    updateUiState { it.copy(gridErrorMessage = null) }
}
