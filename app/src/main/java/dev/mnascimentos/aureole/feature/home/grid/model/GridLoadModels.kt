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

/** The grid limits of each orientation: 20x30 in portrait, 30x20 in landscape. */
fun gridLimitsFor(isLandscape: Boolean): GridLimits = if (isLandscape) {
    GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
} else {
    GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
}

/**
 * Applies a finished load; the active grid and its limits follow [isLandscape], which comes from the current
 * `Configuration` (RF-12). An edit in progress is never overwritten.
 */
fun MainUiState.withLoadedGrid(loaded: LoadedGrid, isLandscape: Boolean): MainUiState {
    if (isGridEditMode) return this
    return copy(
        portraitGridItems = loaded.portrait,
        landscapeGridItems = loaded.landscape,
        gridItems = if (isLandscape) loaded.landscape else loaded.portrait,
        gridLimits = gridLimitsFor(isLandscape),
        isLandscape = isLandscape
    )
}

/**
 * A measure of the grid ([limits], [measuredLandscape]) only fits the items to the limits; it never decides the
 * orientation on its own (RF-12): a frame measured for the other orientation is transient, or a rotation whose
 * load (from the `Configuration`, in `MainLifecycleObserver`) is on its way, and is ignored. During a grid edit the
 * loads are held back, so there the measure still moves the edit to the new orientation, as before.
 */
fun MainUiState.withMeasuredLimits(
    fallback: LoadedGrid,
    limits: GridLimits,
    measuredLandscape: Boolean
): GridOrientationResult {
    if (!isMeasureChange(limits, measuredLandscape)) return GridOrientationResult(this, null)
    return withGridOrientation(fallback, limits, measuredOrientation(measuredLandscape))
}

private fun MainUiState.measuredOrientation(measuredLandscape: Boolean): Boolean =
    if (isGridEditMode) measuredLandscape else isLandscape

/** Whether a measure changes anything: limits of the orientation the state is in, and not the ones it has. */
fun MainUiState.isMeasureChange(limits: GridLimits, measuredLandscape: Boolean): Boolean {
    val target = measuredOrientation(measuredLandscape)
    return gridLimitsFor(target) == limits && (target != isLandscape || gridLimits != limits)
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
