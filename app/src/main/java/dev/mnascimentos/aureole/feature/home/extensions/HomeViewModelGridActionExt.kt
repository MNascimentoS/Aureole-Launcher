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
    widgetId: Int? = null
) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val (spans, minSpans) = GridEngineUtils.getDefaultSpanForType(type)
        val (colSpan, rowSpan) = spans
        val (minColSpan, minRowSpan) = minSpans

        val availableSlot = GridEngineUtils.findFirstAvailableSlot(colSpan, rowSpan, currentItems)
        if (availableSlot == null) {
            state.copy(gridErrorMessage = CONTAINER_ERROR_MSG)
        } else {
            val newItem = LauncherItemState(
                id = UUID.randomUUID().toString(),
                type = type,
                col = availableSlot.first,
                row = availableSlot.second,
                colSpan = colSpan,
                rowSpan = rowSpan,
                minColSpan = minColSpan,
                minRowSpan = minRowSpan,
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

fun HomeViewModel.addSingleWidgetGridItem(widgetId: Int) {
    addGridItem(LauncherItemType.SINGLE_APP_WIDGET, widgetId = widgetId)
}
