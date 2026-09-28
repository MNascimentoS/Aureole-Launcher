package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.GridLimits
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal fun HomeViewModel.getGridRepository(): GridRepository {
    return GridRepository(getApplication())
}

fun HomeViewModel.loadGridItems(isLandscape: Boolean = false) {
    if (uiState.value.isGridEditMode) return
    val portraitItems = getGridRepository().getGridItems(isLandscape = false)
    val landscapeItems = getGridRepository().getGridItems(isLandscape = true)
    updateUiState {
        it.copy(
            portraitGridItems = portraitItems,
            landscapeGridItems = landscapeItems,
            gridItems = if (isLandscape) landscapeItems else portraitItems,
            isLandscape = isLandscape
        )
    }
}

fun HomeViewModel.enterGridEditMode() {
    updateUiState {
        it.copy(
            isGridEditMode = true,
            cachedPortraitGridItems = it.portraitGridItems,
            cachedLandscapeGridItems = it.landscapeGridItems,
            cachedGridItems = it.gridItems
        )
    }
}

fun HomeViewModel.cancelGridEditMode() {
    updateUiState { state ->
        val restoredPortrait = state.cachedPortraitGridItems ?: state.portraitGridItems
        val restoredLandscape = state.cachedLandscapeGridItems ?: state.landscapeGridItems
        val restoredGridItems = if (state.isLandscape) restoredLandscape else restoredPortrait
        state.copy(
            isGridEditMode = false,
            portraitGridItems = restoredPortrait,
            landscapeGridItems = restoredLandscape,
            gridItems = restoredGridItems,
            cachedPortraitGridItems = null,
            cachedLandscapeGridItems = null,
            cachedGridItems = null
        )
    }
}

fun HomeViewModel.saveGridEditMode() {
    val portraitToSave = uiState.value.portraitGridItems
    val landscapeToSave = uiState.value.landscapeGridItems
    getGridRepository().saveGridItems(portraitToSave, isLandscape = false)
    getGridRepository().saveGridItems(landscapeToSave, isLandscape = true)
    updateUiState {
        it.copy(
            isGridEditMode = false,
            cachedPortraitGridItems = null,
            cachedLandscapeGridItems = null,
            cachedGridItems = null
        )
    }
}

fun HomeViewModel.updateGridItemsOrientation(maxCols: Int, maxRows: Int) {
    val limits = GridLimits(maxCols, maxRows)
    val isLandscape = (maxCols == GridEngineUtils.LANDSCAPE_MAX_COLS)

    updateUiState { state ->
        if (state.isLandscape == isLandscape && state.gridLimits == limits) {
            return@updateUiState state
        }

        val portraitItems = if (state.portraitGridItems.isNotEmpty()) {
            state.portraitGridItems
        } else {
            getGridRepository().getGridItems(isLandscape = false)
        }

        val landscapeItems = if (state.landscapeGridItems.isNotEmpty()) {
            state.landscapeGridItems
        } else {
            getGridRepository().getGridItems(isLandscape = true)
        }

        val activeItems = if (isLandscape) landscapeItems else portraitItems
        val constrainedActiveItems = GridEngineUtils.constrainItemsToBounds(activeItems, limits)

        val newPortrait = if (!isLandscape) constrainedActiveItems else portraitItems
        val newLandscape = if (isLandscape) constrainedActiveItems else landscapeItems

        if (!state.isGridEditMode && activeItems != constrainedActiveItems) {
            getGridRepository().saveGridItems(constrainedActiveItems, isLandscape)
        }

        state.copy(
            gridItems = constrainedActiveItems,
            portraitGridItems = newPortrait,
            landscapeGridItems = newLandscape,
            gridLimits = limits,
            isLandscape = isLandscape
        )
    }
}

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

fun HomeViewModel.resetGridItems() {
    getGridRepository().resetToDefault()
    updateUiState {
        it.copy(
            isGridEditMode = false,
            cachedPortraitGridItems = null,
            cachedLandscapeGridItems = null,
            cachedGridItems = null
        )
    }
    loadGridItems(uiState.value.isLandscape)
}
