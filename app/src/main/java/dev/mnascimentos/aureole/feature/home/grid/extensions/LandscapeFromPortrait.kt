package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest

private val LANDSCAPE_LIMITS = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)

/**
 * The first landscape layout of a home that already has a [portrait] one: the same items (same ids,
 * so favorites and widgets are shared) fitted into the default landscape layout v4. An item takes
 * the spot of its type; the ones with no spot (a second clock, quick buttons, scroll views...) go to
 * the largest free slot, and are left out only when the grid has no room for them.
 * [portrait] is already in the user's handedness, and the default spots are mirrored for left-handed.
 */
fun landscapeFromPortrait(portrait: List<LauncherItemState>, isLeftHanded: Boolean): List<LauncherItemState> {
    val taken = mutableSetOf<LauncherItemType>()
    val (inMold, extras) = portrait.partition { item ->
        val type = item.safeType
        val hasSlot = GridDefaults.getDefaultItem(type, isLandscape = true, isLeftHanded = isLeftHanded) != null
        hasSlot && taken.add(type)
    }
    val placed = inMold.map { item ->
        val slot = checkNotNull(
            GridDefaults.getDefaultItem(item.safeType, isLandscape = true, isLeftHanded = isLeftHanded)
        )
        item.copy(col = slot.col, row = slot.row, colSpan = slot.colSpan, rowSpan = slot.rowSpan)
    }
    return extras.fold(placed) { items, item -> items + placeInLargestSlot(item, items) }
}

private fun placeInLargestSlot(item: LauncherItemState, items: List<LauncherItemState>): List<LauncherItemState> {
    val found = GridEngineUtils.findLargestAvailableSlot(
        request = SlotSearchRequest(
            targetColSpan = item.colSpan,
            targetRowSpan = item.rowSpan,
            minColSpan = item.minColSpan,
            minRowSpan = item.minRowSpan,
            limits = LANDSCAPE_LIMITS
        ),
        items = items
    ) ?: return emptyList()
    val (colSpan, rowSpan, slot) = found
    return listOf(item.copy(col = slot.first, row = slot.second, colSpan = colSpan, rowSpan = rowSpan))
}

/**
 * The first time the landscape opens after the onboarding (its layout is empty or was never saved) while
 * the portrait home has items, the landscape is built from them and saved. A layout saved with items is
 * never touched. Only once the onboarding is done: while it runs the user is still building the home.
 */
internal fun HomeViewModel.orFirstLandscapeLayout(
    saved: List<LauncherItemState>,
    portrait: List<LauncherItemState>,
    isLeftHanded: Boolean
): List<LauncherItemState> {
    val isFirstOpen = saved.isEmpty() || !getGridRepository().hasSavedLandscapeLayout()
    val isOnboardingDone = uiState.value.onboarding.stage == OnboardingStage.COMPLETED
    if (!isFirstOpen || portrait.isEmpty() || !isOnboardingDone) return saved
    return landscapeFromPortrait(portrait, isLeftHanded).also {
        getGridRepository().saveGridItems(it, isLandscape = true)
    }
}
