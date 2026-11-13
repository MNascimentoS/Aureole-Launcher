package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.dropClockConfig
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.giveNewClockItsConfig
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.hasItemOnAnyLayout
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.dropContainers
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.dropFavoritesList
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemSpec
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.util.UUID

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

/**
 * Adds an item of [spec] in the largest free slot (or to the scroll view being filled) and enters edit mode. A new
 * clock also gets its own config, a copy of the default look.
 */
fun HomeViewModel.addGridItem(spec: GridItemSpec) {
    val newId = UUID.randomUUID().toString()
    placeGridItem(spec, newId)
    val isPlacedClock = spec.type == LauncherItemType.CLOCK && uiState.value.hasItemOnAnyLayout(newId)
    if (isPlacedClock) giveNewClockItsConfig(newId)
}

private fun HomeViewModel.placeGridItem(spec: GridItemSpec, newId: String) {
    val currentParentId = uiState.value.targetParentContainerId
    if (currentParentId != null) {
        addChildToScrollView(currentParentId, spec.type, spec.widgetId, childId = newId)
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
                minRowSpan = reqMinRowSpan,
                limits = state.gridLimits
            ),
            items = currentItems
        )

        if (largestSlotResult == null) {
            state.copy(gridErrorMessage = TextResource.fromResId(R.string.home_grid_no_space_error))
        } else {
            val (actualColSpan, actualRowSpan, slot) = largestSlotResult
            val newItem = LauncherItemState(
                id = newId,
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
            withNewGridItemInEditMode(state, newItem)
        }
    }
}

/** Adds [newItem] to the active grid and enters edit mode, keeping the layout to restore on cancel. */
internal fun withNewGridItemInEditMode(state: MainUiState, newItem: LauncherItemState): MainUiState {
    val newStateWithGrid = updateActiveGridItemsInState(state, state.gridItems + newItem).copy(
        showAddContainerDialog = false,
        targetParentContainerId = null,
        isGridEditMode = true
    )
    return if (!state.isGridEditMode) {
        newStateWithGrid.copy(
            cachedPortraitGridItems = state.portraitGridItems,
            cachedLandscapeGridItems = state.landscapeGridItems,
            cachedGridItems = state.gridItems
        )
    } else {
        newStateWithGrid
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

/**
 * Removes the item and offers "Undo", which puts it back in the same cell. A favorites list also loses its saved
 * favorites right away, a clock its config and a container (or the containers of a scroll group) its items,
 * folders and config; the undo writes them back (see [dropFavoritesList], [dropClockConfig] and [dropContainers]).
 */
fun HomeViewModel.removeGridItemWithUndo(id: String) {
    val item = uiState.value.gridItems.find { it.id == id }
    val restoreFavorites = if (item?.safeType == LauncherItemType.APPS_LIST) dropFavoritesList(id) else null
    deleteGridItem(id)
    if (item == null) return
    // After the delete, so a clock with the same id still in the other layout keeps the shared config.
    val restoreClock = if (item.safeType == LauncherItemType.CLOCK) dropClockConfig(id) else null
    val restoreContainers = dropContainers(listOf(item))
    showUndo(TextResource.fromResId(R.string.home_undo_component_removed)) {
        restoreGridItem(item)
        restoreFavorites?.invoke()
        restoreClock?.invoke()
        restoreContainers()
    }
}

internal fun HomeViewModel.restoreGridItem(item: LauncherItemState) {
    updateUiState { state ->
        if (state.gridItems.any { it.id == item.id }) {
            state
        } else {
            val restored = state.gridItems + item
            if (!state.isGridEditMode) {
                getGridRepository().saveGridItems(restored, state.isLandscape)
            }
            updateActiveGridItemsInState(state, restored)
        }
    }
}

fun HomeViewModel.dismissGridError() {
    updateUiState { it.copy(gridErrorMessage = null) }
}
