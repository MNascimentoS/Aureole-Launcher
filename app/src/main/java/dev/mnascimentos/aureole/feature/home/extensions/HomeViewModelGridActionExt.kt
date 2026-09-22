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
private const val MAX_CHILD_SPAN = 3

data class GridItemSpec(
    val type: LauncherItemType,
    val widgetId: Int? = null,
    val targetColSpan: Int? = null,
    val targetRowSpan: Int? = null,
    val minColSpan: Int? = null,
    val minRowSpan: Int? = null
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

fun HomeViewModel.openAddContainerForParent(parentId: String) {
    updateUiState { it.copy(targetParentContainerId = parentId, showAddContainerDialog = true) }
}

fun HomeViewModel.updateScrollViewOrientation(parentId: String, orientation: ScrollOrientation) {
    updateUiState { state ->
        val updatedItems = state.gridItems.map { item ->
            if (item.id == parentId) {
                item.copy(scrollOrientation = orientation)
            } else {
                item
            }
        }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedItems, state.isLandscape)
        }
        val updatedEditingItem = if (state.editingGridItem?.id == parentId) {
            state.editingGridItem.copy(scrollOrientation = orientation)
        } else {
            state.editingGridItem
        }
        updateActiveGridItemsInState(state, updatedItems).copy(editingGridItem = updatedEditingItem)
    }
}

fun HomeViewModel.addChildToScrollView(parentId: String, type: LauncherItemType, widgetId: Int? = null) {
    updateUiState { state ->
        val (defaultSpans, defaultMinSpans) = GridDefaults.getDefaultSpanForType(type)
        val newChild = LauncherItemState(
            id = UUID.randomUUID().toString(),
            type = type,
            col = 0,
            row = 0,
            colSpan = defaultSpans.first.coerceAtMost(MAX_CHILD_SPAN),
            rowSpan = defaultSpans.second.coerceAtMost(MAX_CHILD_SPAN),
            minColSpan = defaultMinSpans.first,
            minRowSpan = defaultMinSpans.second,
            widgetId = widgetId
        )
        val updatedItems = state.gridItems.map { item ->
            if (item.id == parentId) {
                item.copy(children = item.safeChildren + newChild)
            } else {
                item
            }
        }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedItems, state.isLandscape)
        }
        val updatedEditingItem = if (state.editingGridItem?.id == parentId) {
            state.editingGridItem.copy(children = state.editingGridItem.safeChildren + newChild)
        } else {
            state.editingGridItem
        }
        updateActiveGridItemsInState(state, updatedItems).copy(
            editingGridItem = updatedEditingItem,
            showAddContainerDialog = false,
            targetParentContainerId = null,
            isAddingSingleWidget = false
        )
    }
}

fun HomeViewModel.resizeChildInScrollView(
    parentId: String,
    childId: String,
    newColSpan: Int,
    newRowSpan: Int
) {
    updateUiState { state ->
        val updatedItems = state.gridItems.map { item ->
            if (item.id == parentId) {
                val updatedChildren = item.safeChildren.map { child ->
                    if (child.id == childId) {
                        child.copy(
                            colSpan = newColSpan.coerceAtLeast(1),
                            rowSpan = newRowSpan.coerceAtLeast(1)
                        )
                    } else {
                        child
                    }
                }
                item.copy(children = updatedChildren)
            } else {
                item
            }
        }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedItems, state.isLandscape)
        }
        val updatedEditingItem = if (state.editingGridItem?.id == parentId) {
            val updatedChildren = state.editingGridItem.safeChildren.map { child ->
                if (child.id == childId) {
                    child.copy(
                        colSpan = newColSpan.coerceAtLeast(1),
                        rowSpan = newRowSpan.coerceAtLeast(1)
                    )
                } else {
                    child
                }
            }
            state.editingGridItem.copy(children = updatedChildren)
        } else {
            state.editingGridItem
        }
        updateActiveGridItemsInState(state, updatedItems).copy(editingGridItem = updatedEditingItem)
    }
}

fun HomeViewModel.removeChildFromScrollView(parentId: String, childId: String) {
    updateUiState { state ->
        val updatedItems = state.gridItems.map { item ->
            if (item.id == parentId) {
                item.copy(children = item.safeChildren.filterNot { it.id == childId })
            } else {
                item
            }
        }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedItems, state.isLandscape)
        }
        val updatedEditingItem = if (state.editingGridItem?.id == parentId) {
            state.editingGridItem.copy(children = state.editingGridItem.safeChildren.filterNot { it.id == childId })
        } else {
            state.editingGridItem
        }
        updateActiveGridItemsInState(state, updatedItems).copy(editingGridItem = updatedEditingItem)
    }
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
                widgetId = spec.widgetId
            )
            val updatedList = currentItems + newItem
            if (!state.isGridEditMode) {
                getGridRepository().saveGridItems(updatedList, state.isLandscape)
            }
            updateActiveGridItemsInState(state, updatedList).copy(
                showAddContainerDialog = false,
                targetParentContainerId = null
            )
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
