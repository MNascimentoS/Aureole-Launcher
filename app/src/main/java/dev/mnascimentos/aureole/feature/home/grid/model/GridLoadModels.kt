package dev.mnascimentos.aureole.feature.home.grid.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The two layouts read from disk by one load, before they reach the UI state. */
data class LoadedGrid(
    val portrait: List<LauncherItemState>,
    val landscape: List<LauncherItemState>
)

/** What a grid orientation update decided: the next state and, only when the items changed, what to save. */
data class GridOrientationResult(
    val state: MainUiState,
    val itemsToSave: List<LauncherItemState>?
)

/** Applies a finished load; the active grid follows [isLandscape]. An edit in progress is never overwritten. */
fun MainUiState.withLoadedGrid(loaded: LoadedGrid, isLandscape: Boolean): MainUiState {
    if (isGridEditMode) return this
    return copy(
        portraitGridItems = loaded.portrait,
        landscapeGridItems = loaded.landscape,
        gridItems = if (isLandscape) loaded.landscape else loaded.portrait,
        isLandscape = isLandscape
    )
}

/**
 * Fits the active layout to [limits] and flips the orientation, with no I/O: [fallback] stands in for a
 * layout that is not in memory yet. [GridOrientationResult.itemsToSave] is set only when the fit changed
 * the items and no edit is in progress.
 */
fun MainUiState.withGridOrientation(
    fallback: LoadedGrid,
    limits: GridLimits,
    isLandscape: Boolean
): GridOrientationResult {
    if (this.isLandscape == isLandscape && gridLimits == limits) return GridOrientationResult(this, null)
    val portraitItems = portraitGridItems.ifEmpty { fallback.portrait }
    val landscapeItems = landscapeGridItems.ifEmpty { fallback.landscape }
    val activeItems = if (isLandscape) landscapeItems else portraitItems
    val constrained = GridEngineUtils.constrainItemsToBounds(activeItems, limits)
    val next = copy(
        gridItems = constrained,
        portraitGridItems = if (isLandscape) portraitItems else constrained,
        landscapeGridItems = if (isLandscape) constrained else landscapeItems,
        gridLimits = limits,
        isLandscape = isLandscape
    )
    val itemsToSave = constrained.takeIf { !isGridEditMode && activeItems != it }
    return GridOrientationResult(next, itemsToSave)
}
