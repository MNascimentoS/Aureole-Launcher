package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import java.util.UUID

private const val CONTAINER_ERROR_MSG = "Não há espaço livre suficiente na grade para este container."

fun HomeViewModel.setShowAddContainerDialog(show: Boolean) {
    updateUiState { it.copy(showAddContainerDialog = show) }
}

fun HomeViewModel.setEditingGridItem(item: LauncherItemState?) {
    updateUiState { it.copy(editingGridItem = item) }
}

fun HomeViewModel.addGridItem(
    type: LauncherItemType,
    widgetId: Int? = null,
    targetColSpan: Int? = null,
    targetRowSpan: Int? = null,
    minColSpan: Int? = null,
    minRowSpan: Int? = null
) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val (defaultSpans, defaultMinSpans) = GridEngineUtils.getDefaultSpanForType(type)
        
        val reqTargetColSpan = targetColSpan ?: defaultSpans.first
        val reqTargetRowSpan = targetRowSpan ?: defaultSpans.second
        val reqMinColSpan = minColSpan ?: defaultMinSpans.first
        val reqMinRowSpan = minRowSpan ?: defaultMinSpans.second

        val largestSlotResult = GridEngineUtils.findLargestAvailableSlot(
            targetColSpan = reqTargetColSpan,
            targetRowSpan = reqTargetRowSpan,
            minColSpan = reqMinColSpan,
            minRowSpan = reqMinRowSpan,
            items = currentItems
        )

        if (largestSlotResult == null) {
            state.copy(gridErrorMessage = CONTAINER_ERROR_MSG)
        } else {
            val (actualColSpan, actualRowSpan, slot) = largestSlotResult
            val newItem = LauncherItemState(
                id = UUID.randomUUID().toString(),
                type = type,
                col = slot.first,
                row = slot.second,
                colSpan = actualColSpan,
                rowSpan = actualRowSpan,
                minColSpan = reqMinColSpan,
                minRowSpan = reqMinRowSpan,
                widgetId = widgetId
            )
            val updatedList = currentItems + newItem
            getGridRepository().saveGridItems(updatedList)
            state.copy(gridItems = updatedList, showAddContainerDialog = false)
        }
    }
}

fun HomeViewModel.deleteGridItem(id: String) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val updatedList = currentItems.filterNot { it.id == id }
        getGridRepository().saveGridItems(updatedList)
        state.copy(gridItems = updatedList, editingGridItem = null)
    }
}

fun HomeViewModel.dismissGridError() {
    updateUiState { it.copy(gridErrorMessage = null) }
}

fun HomeViewModel.setIsAddingSingleWidget(isAdding: Boolean) {
    updateUiState { it.copy(isAddingSingleWidget = isAdding) }
}

fun HomeViewModel.addSingleWidgetGridItem(
    widgetId: Int,
    targetSpanX: Int? = null,
    targetSpanY: Int? = null,
    minSpanX: Int? = null,
    minSpanY: Int? = null
) {
    addGridItem(
        type = LauncherItemType.SINGLE_APP_WIDGET,
        widgetId = widgetId,
        targetColSpan = targetSpanX,
        targetRowSpan = targetSpanY,
        minColSpan = minSpanX,
        minRowSpan = minSpanY
    )
}
