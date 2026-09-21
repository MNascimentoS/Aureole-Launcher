package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

internal fun HomeViewModel.getGridRepository(): GridRepository {
    return GridRepository(getApplication())
}

fun HomeViewModel.loadGridItems() {
    val items = getGridRepository().getGridItems()
    updateUiState { it.copy(gridItems = items) }
}

fun HomeViewModel.toggleGridEditMode() {
    updateUiState { it.copy(isGridEditMode = !it.isGridEditMode) }
}

fun HomeViewModel.moveGridItem(id: String, newCol: Int, newRow: Int) {
    val currentItems = uiState.value.gridItems
    val targetIndex = currentItems.indexOfFirst { it.id == id }
    if (targetIndex != -1) {
        val item = currentItems[targetIndex]
        val updatedItem = item.copy(col = newCol, row = newRow)
        val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, currentItems)
        val withinBounds = GridEngineUtils.isWithinBounds(
            newCol, newRow, item.colSpan, item.rowSpan
        )
        if (!hasCollision && withinBounds) {
            val updatedList = currentItems.toMutableList().apply {
                this[targetIndex] = updatedItem
            }
            updateUiState { it.copy(gridItems = updatedList) }
            getGridRepository().saveGridItems(updatedList)
        }
    }
}

fun HomeViewModel.resizeGridItem(id: String, newColSpan: Int, newRowSpan: Int) {
    val currentItems = uiState.value.gridItems
    val targetIndex = currentItems.indexOfFirst { it.id == id }
    if (targetIndex != -1) {
        val item = currentItems[targetIndex]
        val updatedItem = item.copy(colSpan = newColSpan, rowSpan = newRowSpan)
        val hasCollision = GridEngineUtils.checkCollisionWithOthers(updatedItem, currentItems)
        val withinBounds = GridEngineUtils.isWithinBounds(
            item.col, item.row, newColSpan, newRowSpan
        )
        if (!hasCollision && withinBounds) {
            val updatedList = currentItems.toMutableList().apply {
                this[targetIndex] = updatedItem
            }
            updateUiState { it.copy(gridItems = updatedList) }
            getGridRepository().saveGridItems(updatedList)
        }
    }
}

fun HomeViewModel.resetGridItems() {
    getGridRepository().resetToDefault()
    loadGridItems()
}
