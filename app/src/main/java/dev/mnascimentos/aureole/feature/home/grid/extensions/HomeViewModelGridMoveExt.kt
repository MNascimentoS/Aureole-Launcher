package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal fun updateActiveGridItemsInState(
    state: MainUiState,
    updatedList: List<LauncherItemState>
): MainUiState {
    val newPortrait = if (!state.isLandscape) updatedList else state.portraitGridItems
    val newLandscape = if (state.isLandscape) updatedList else state.landscapeGridItems
    return state.copy(
        gridItems = updatedList,
        portraitGridItems = newPortrait,
        landscapeGridItems = newLandscape
    )
}

fun HomeViewModel.moveGridItem(id: String, newCol: Int, newRow: Int) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val targetIndex = currentItems.indexOfFirst { it.id == id }
        if (targetIndex != -1) {
            val item = currentItems[targetIndex]
            val updatedItem = item.copy(col = newCol, row = newRow)

            val otherItems = currentItems.filterNot { it.id == id }
            val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, otherItems)

            val withinBounds = GridEngineUtils.isWithinBounds(
                newCol,
                newRow,
                item.colSpan,
                item.rowSpan,
                state.gridLimits
            )
            if (!hasCollision && withinBounds) {
                val updatedList = currentItems.toMutableList().apply {
                    this[targetIndex] = updatedItem
                }
                if (!state.isGridEditMode) {
                    getGridRepository().saveGridItems(updatedList, state.isLandscape)
                }
                updateActiveGridItemsInState(state, updatedList)
            } else {
                state
            }
        } else {
            state
        }
    }
}

fun HomeViewModel.resizeGridItem(id: String, newColSpan: Int, newRowSpan: Int) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val targetIndex = currentItems.indexOfFirst { it.id == id }
        if (targetIndex != -1) {
            val item = currentItems[targetIndex]
            val updatedItem = item.copy(colSpan = newColSpan, rowSpan = newRowSpan)

            val otherItems = currentItems.filterNot { it.id == id }
            val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, otherItems)

            val withinBounds = GridEngineUtils.isWithinBounds(
                item.col,
                item.row,
                newColSpan,
                newRowSpan,
                state.gridLimits
            )
            if (!hasCollision && withinBounds) {
                val updatedList = currentItems.toMutableList().apply {
                    this[targetIndex] = updatedItem
                }
                if (!state.isGridEditMode) {
                    getGridRepository().saveGridItems(updatedList, state.isLandscape)
                }
                updateActiveGridItemsInState(state, updatedList)
            } else {
                state
            }
        } else {
            state
        }
    }
}
