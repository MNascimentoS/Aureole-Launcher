package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.completeLayoutOnOwnSave

/** Saves the grid edit mode; with the onboarding list on screen and no task pending, that completes the layout step. */
fun HomeViewModel.saveGridEditMode() {
    val hadTask = uiState.value.onboarding.pendingTask != null
    persistGridEditMode()
    if (!hadTask) completeLayoutOnOwnSave()
}

/** Saves both layouts and leaves the edit mode, with no onboarding bookkeeping. */
internal fun HomeViewModel.persistGridEditMode() {
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
