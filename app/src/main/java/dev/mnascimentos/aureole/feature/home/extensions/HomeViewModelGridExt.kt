package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.GridLimits

internal fun HomeViewModel.getGridRepository(): GridRepository {
    return GridRepository(getApplication())
}

fun HomeViewModel.loadGridItems() {
    val items = getGridRepository().getGridItems()
    updateUiState { it.copy(gridItems = items) }
}

fun HomeViewModel.enterGridEditMode() {
    updateUiState { 
        it.copy(
            isGridEditMode = true,
            cachedGridItems = it.gridItems
        ) 
    }
}

fun HomeViewModel.cancelGridEditMode() {
    updateUiState { 
        val restoredItems = it.cachedGridItems ?: it.gridItems
        it.copy(
            isGridEditMode = false,
            gridItems = restoredItems,
            cachedGridItems = null
        ) 
    }
}

fun HomeViewModel.saveGridEditMode() {
    val itemsToSave = uiState.value.gridItems
    getGridRepository().saveGridItems(itemsToSave)
    updateUiState { 
        it.copy(
            isGridEditMode = false,
            cachedGridItems = null
        ) 
    }
}

fun HomeViewModel.updateGridItemsOrientation(maxCols: Int, maxRows: Int) {
    val limits = GridLimits(maxCols, maxRows)
    val currentItems = uiState.value.gridItems
    val constrainedItems = GridEngineUtils.constrainItemsToBounds(currentItems, limits)
    
    // Only save if items actually changed to prevent unnecessary writes
    if (currentItems != constrainedItems) {
        updateUiState { it.copy(gridItems = constrainedItems, gridLimits = limits) }
        getGridRepository().saveGridItems(constrainedItems)
    } else {
        updateUiState { it.copy(gridLimits = limits) }
    }
}

fun HomeViewModel.moveGridItem(id: String, newCol: Int, newRow: Int) {
    updateUiState { state ->
        val currentItems = state.gridItems
        val targetIndex = currentItems.indexOfFirst { it.id == id }
        if (targetIndex != -1) {
            val item = currentItems[targetIndex]
            val updatedItem = item.copy(col = newCol, row = newRow)
            
            // Generate a filtered list removing the original item before collision checking
            val otherItems = currentItems.filterNot { it.id == id }
            val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, otherItems)
            
            val withinBounds = GridEngineUtils.isWithinBounds(
                newCol, newRow, item.colSpan, item.rowSpan, state.gridLimits
            )
            if (!hasCollision && withinBounds) {
                val updatedList = currentItems.toMutableList().apply {
                    this[targetIndex] = updatedItem
                }
                if (!state.isGridEditMode) {
                    getGridRepository().saveGridItems(updatedList)
                }
                state.copy(gridItems = updatedList)
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
            
            // Generate a filtered list removing the original item before collision checking
            val otherItems = currentItems.filterNot { it.id == id }
            val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, otherItems)
            
            val withinBounds = GridEngineUtils.isWithinBounds(
                item.col, item.row, newColSpan, newRowSpan, state.gridLimits
            )
            if (!hasCollision && withinBounds) {
                val updatedList = currentItems.toMutableList().apply {
                    this[targetIndex] = updatedItem
                }
                if (!state.isGridEditMode) {
                    getGridRepository().saveGridItems(updatedList)
                }
                state.copy(gridItems = updatedList)
            } else {
                state
            }
        } else {
            state
        }
    }
}

fun HomeViewModel.resetGridItems() {
    getGridRepository().resetToDefault()
    loadGridItems()
}
