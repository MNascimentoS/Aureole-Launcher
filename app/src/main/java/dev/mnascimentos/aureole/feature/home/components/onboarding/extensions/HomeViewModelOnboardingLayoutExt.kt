package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.giveNewClockItsConfig
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItemsNow
import dev.mnascimentos.aureole.feature.home.grid.extensions.withNewGridItemInEditMode
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemSpec
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.util.UUID

/**
 * A fresh install starts with an empty home that the checklist fills step by step. Guarded by the
 * saved layout so an install that already has one (an old version without a version code) keeps it.
 */
internal fun HomeViewModel.startWithEmptyHome() {
    if (gridRepository.hasSavedLayout()) return
    gridRepository.saveGridItems(emptyList(), isLandscape = false)
    gridRepository.saveGridItems(emptyList(), isLandscape = true)
    loadGridItemsNow(uiState.value.isLandscape)
}

/**
 * Leaving the checklist with nothing built must not leave the home empty: portrait gets the default layout and
 * landscape the onboarding one (the default, with the portrait widgets and settings). A layout that already has
 * items is never touched.
 */
internal fun HomeViewModel.applyDefaultLayoutWhereEmpty() {
    val state = uiState.value
    val isLeftHanded = settingsRepository.isLeftHandedMode
    val portrait = state.portraitGridItems.ifEmpty {
        GridDefaults.getDefaultGridItems(isLandscape = false, isLeftHanded = isLeftHanded)
            .also { gridRepository.saveGridItems(it, isLandscape = false) }
    }
    if (state.landscapeGridItems.isEmpty()) {
        gridRepository.saveGridItems(onboardingLandscapeLayout(portrait, isLeftHanded), isLandscape = true)
    }
    loadGridItemsNow(state.isLandscape)
}

/**
 * The id of the first [type] item on the home. When there is none, creates it where the default
 * layout puts it, or in the largest free slot if that spot is taken. Either way the grid ends up in
 * edit mode; null means the grid is full (the grid error shows).
 */
internal fun HomeViewModel.ensureGridItem(type: LauncherItemType): String? {
    val existing = uiState.value.gridItems.firstOrNull { it.safeType == type }
    if (existing != null) {
        if (!uiState.value.isGridEditMode) enterGridEditMode()
        return existing.id
    }
    return placeNewGridItem(type)
}

/**
 * Always adds a new item of [type], even when the home already has one: at the layout's default spot if it is free,
 * else in the largest free slot. With [alwaysNewId] the id is a fresh UUID, never the layout's stable one, so the
 * item cannot pick up data that an earlier item with that stable id left behind. A new clock gets its own config.
 * Null means the grid is full.
 */
internal fun HomeViewModel.placeNewGridItem(type: LauncherItemType, alwaysNewId: Boolean = false): String? =
    (placeAtDefaultPosition(type, alwaysNewId) ?: placeInLargestSlot(type))?.also { id ->
        if (type == LauncherItemType.CLOCK) giveNewClockItsConfig(id)
    }

private fun HomeViewModel.placeAtDefaultPosition(type: LauncherItemType, alwaysNewId: Boolean): String? {
    val state = uiState.value
    val default = GridDefaults.getDefaultItem(type, state.isLandscape, settingsRepository.isLeftHandedMode)
    // The stable id can already be in use, for example by an item of another type after a reset.
    val item = default?.let { candidate ->
        val isIdTaken = alwaysNewId || state.gridItems.any { it.id == candidate.id }
        if (isIdTaken) candidate.copy(id = UUID.randomUUID().toString()) else candidate
    }
    if (item == null || !item.fitsIn(state)) return null
    updateUiState { withNewGridItemInEditMode(it, item) }
    return item.id
}

private fun LauncherItemState.fitsIn(state: MainUiState): Boolean =
    GridEngineUtils.isWithinBounds(col, row, colSpan, rowSpan, state.gridLimits) &&
        !GridEngineUtils.checkCollisionWithOthers(this, state.gridItems)

private fun HomeViewModel.placeInLargestSlot(type: LauncherItemType): String? {
    val before = uiState.value.gridItems.map { it.id }.toSet()
    addGridItem(GridItemSpec(type = type))
    return uiState.value.gridItems.firstOrNull { it.id !in before }?.id
}
