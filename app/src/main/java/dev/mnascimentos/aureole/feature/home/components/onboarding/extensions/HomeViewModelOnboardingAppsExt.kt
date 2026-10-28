package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.util.UUID

/** Runs after every state update: the onboarding never ends with a home that has no way to the apps. */
internal fun HomeViewModel.ensureAppsOnCompletion(previous: MainUiState) {
    val isCompletedNow = previous.onboarding.stage != OnboardingStage.COMPLETED &&
        uiState.value.onboarding.stage == OnboardingStage.COMPLETED
    if (isCompletedNow) ensureAppsComponent()
}

/**
 * In both orientations: keeps a favorites list that has favorites or an all apps list; turns an
 * empty favorites list into the all apps list in place; otherwise adds the all apps list at the
 * default apps spot, or in the largest free slot. A full grid gets nothing and no warning.
 */
internal fun HomeViewModel.ensureAppsComponent() {
    val state = uiState.value
    val isLeftHanded = settingsRepository.isLeftHandedMode
    val portrait = state.withAppsComponent(state.portraitGridItems, isLandscape = false, isLeftHanded = isLeftHanded)
    val landscape = state.withAppsComponent(state.landscapeGridItems, isLandscape = true, isLeftHanded = isLeftHanded)
    if (portrait == state.portraitGridItems && landscape == state.landscapeGridItems) return
    gridRepository.saveGridItems(portrait, isLandscape = false)
    gridRepository.saveGridItems(landscape, isLandscape = true)
    updateUiState {
        it.copy(
            portraitGridItems = portrait,
            landscapeGridItems = landscape,
            gridItems = if (it.isLandscape) landscape else portrait
        )
    }
}

private fun MainUiState.withAppsComponent(
    items: List<LauncherItemState>,
    isLandscape: Boolean,
    isLeftHanded: Boolean
): List<LauncherItemState> {
    val hasApps = items.any {
        it.safeType == LauncherItemType.ALL_APPS_LIST || (it.safeType == LauncherItemType.APPS_LIST && hasFavorites(it))
    }
    val emptyFavorites = items.firstOrNull { it.safeType == LauncherItemType.APPS_LIST }
    return when {
        hasApps -> items
        emptyFavorites != null -> items.map { item ->
            if (item.id == emptyFavorites.id) item.asAllApps() else item
        }
        else -> items + listOfNotNull(placeAllApps(items, isLandscape, isLeftHanded))
    }
}

private fun placeAllApps(
    items: List<LauncherItemState>,
    isLandscape: Boolean,
    isLeftHanded: Boolean
): LauncherItemState? {
    val default = GridDefaults.getDefaultItem(LauncherItemType.ALL_APPS_LIST, isLandscape, isLeftHanded)
        ?.asAllApps() ?: return null
    val limits = if (isLandscape) {
        GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
    } else {
        GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    }
    if (!GridEngineUtils.checkCollisionWithOthers(default, items)) return default
    val request = SlotSearchRequest(targetColSpan = default.colSpan, targetRowSpan = default.rowSpan, limits = limits)
    return GridEngineUtils.findLargestAvailableSlot(request, items)?.let { (colSpan, rowSpan, slot) ->
        default.copy(col = slot.first, row = slot.second, colSpan = colSpan, rowSpan = rowSpan)
    }
}

private fun LauncherItemState.asAllApps() =
    copy(id = UUID.randomUUID().toString(), type = LauncherItemType.ALL_APPS_LIST)
