package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import java.util.UUID

private const val MAX_CHILD_SPAN = 3

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
            isAddingSingleWidget = false,
            gridErrorMessage = null,
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

fun HomeViewModel.moveChildInScrollView(parentId: String, childId: String, moveUp: Boolean) {
    updateUiState { state ->
        val parent = state.gridItems.find { it.id == parentId }
            ?: state.editingGridItem
            ?: return@updateUiState state

        val children = parent.safeChildren.toMutableList()
        val index = children.indexOfFirst { it.id == childId }
        if (index == -1) return@updateUiState state

        val newIndex = if (moveUp) index - 1 else index + 1
        if (newIndex !in 0 until children.size) return@updateUiState state

        val itemToMove = children.removeAt(index)
        children.add(newIndex, itemToMove)

        val updatedItems = state.gridItems.map { item ->
            if (item.id == parentId) {
                item.copy(children = children)
            } else {
                item
            }
        }
        if (!state.isGridEditMode) {
            getGridRepository().saveGridItems(updatedItems, state.isLandscape)
        }
        val updatedEditingItem = if (state.editingGridItem?.id == parentId) {
            state.editingGridItem.copy(children = children)
        } else {
            state.editingGridItem
        }
        updateActiveGridItemsInState(state, updatedItems).copy(editingGridItem = updatedEditingItem)
    }
}
