package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.loadContainers
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.abandonPendingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.keepingCreatedAppsList
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.LoadedGrid
import dev.mnascimentos.aureole.feature.home.grid.model.withGridOrientation
import dev.mnascimentos.aureole.feature.home.grid.model.withLoadedGrid
import kotlinx.coroutines.withContext

internal fun HomeViewModel.getGridRepository(): GridRepository {
    return gridRepository
}

/** Reads both layouts off the main thread; only the latest call applies its result. */
fun HomeViewModel.loadGridItems(isLandscape: Boolean = false) {
    if (uiState.value.isGridEditMode) return
    gridLoads.launch {
        val loaded = withContext(gridLoadDispatcher) { readGridLayouts(isLandscape) }
        applyLoadedGrid(loaded, isLandscape)
    }
}

/**
 * Same as [loadGridItems] but on the calling thread and done when it returns, for flows that read the state right
 * after loading (the onboarding dismiss). It also drops any load still in flight, whose result would be older.
 */
internal fun HomeViewModel.loadGridItemsNow(isLandscape: Boolean = false) {
    if (uiState.value.isGridEditMode) return
    gridLoads.cancel()
    applyLoadedGrid(readGridLayouts(isLandscape), isLandscape)
}

private fun HomeViewModel.readGridLayouts(isLandscape: Boolean): LoadedGrid {
    val isLeftHanded = settingsRepository.isLeftHandedMode
    val portraitItems = getGridRepository().getGridItems(isLandscape = false, isLeftHanded = isLeftHanded)
    val landscapeItems = getGridRepository().getGridItems(isLandscape = true, isLeftHanded = isLeftHanded)
        .let { saved -> if (isLandscape) orFirstLandscapeLayout(saved, portraitItems, isLeftHanded) else saved }
    return LoadedGrid(portraitItems, landscapeItems)
}

private fun HomeViewModel.applyLoadedGrid(loaded: LoadedGrid, isLandscape: Boolean) {
    updateUiState { it.withLoadedGrid(loaded, isLandscape) }
    // A reset or the default layout can bring a container that has no entity yet.
    val hasMissingContainer = (loaded.portrait + loaded.landscape)
        .missingContainerIds(uiState.value.containers.keys)
        .isNotEmpty()
    if (hasMissingContainer) loadContainers()
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

/** Goes back to the layout the edit started from; the favorites task keeps the apps list it created (saved). */
fun HomeViewModel.cancelGridEditMode() {
    var keptAppsList: List<LauncherItemState>? = null
    updateUiState { state ->
        val cachedPortrait = state.cachedPortraitGridItems ?: state.portraitGridItems
        val cachedLandscape = state.cachedLandscapeGridItems ?: state.landscapeGridItems
        val kept = state.keepingCreatedAppsList(if (state.isLandscape) cachedLandscape else cachedPortrait)
        keptAppsList = kept
        val restoredPortrait = if (kept != null && !state.isLandscape) kept else cachedPortrait
        val restoredLandscape = if (kept != null && state.isLandscape) kept else cachedLandscape
        val restoredGridItems = if (state.isLandscape) restoredLandscape else restoredPortrait
        state.copy(
            isGridEditMode = false,
            portraitGridItems = restoredPortrait,
            landscapeGridItems = restoredLandscape,
            gridItems = restoredGridItems,
            cachedPortraitGridItems = null,
            cachedLandscapeGridItems = null,
            cachedGridItems = null,
            // Cancelling undoes the onboarding item too, so its task cannot complete.
            onboarding = state.onboarding.abandonPendingTask()
        )
    }
    keptAppsList?.let { getGridRepository().saveGridItems(it, uiState.value.isLandscape) }
}

fun HomeViewModel.updateGridItemsOrientation(maxCols: Int, maxRows: Int) {
    val limits = GridLimits(maxCols, maxRows)
    val isLandscape = (maxCols == GridEngineUtils.LANDSCAPE_MAX_COLS)
    val current = uiState.value
    if (current.isLandscape == isLandscape && current.gridLimits == limits) return

    // Disk is read here, outside the state lambda, and only for a layout that is not in memory yet.
    val portraitFallback = if (current.portraitGridItems.isEmpty()) {
        getGridRepository().getGridItems(isLandscape = false, isLeftHanded = current.isLeftHandedMode)
    } else {
        emptyList()
    }
    val landscapeFallback = if (current.landscapeGridItems.isEmpty()) {
        getGridRepository().getGridItems(isLandscape = true, isLeftHanded = current.isLeftHandedMode)
    } else {
        emptyList()
    }
    var itemsToSave: List<LauncherItemState>? = null
    updateUiState { state ->
        val loaded = LoadedGrid(portraitFallback, landscapeFallback)
        val result = state.withGridOrientation(loaded, limits, isLandscape)
        itemsToSave = result.itemsToSave
        result.state
    }
    itemsToSave?.let { getGridRepository().saveGridItems(it, isLandscape) }
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
