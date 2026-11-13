package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingItemSize
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPlacementRequest
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

private const val CLOCK_COLS = 10
private const val CLOCK_ROWS = 4
private const val CONTAINER_COLS = 4
private const val CONTAINER_ROWS = 6
private const val WIDGETS_COLS = 14
private const val WIDGETS_ROWS = 6

/**
 * The compact size an onboarding item gets, so no item takes the whole home before the layout step: a short
 * clock, a narrow container and a widget list one row tall. Other types keep their default size.
 */
internal fun onboardingDefaultSize(type: LauncherItemType): OnboardingItemSize = when (type) {
    LauncherItemType.CLOCK -> OnboardingItemSize(CLOCK_COLS, CLOCK_ROWS)
    LauncherItemType.SHORTCUTS_CONTAINER -> OnboardingItemSize(CONTAINER_COLS, CONTAINER_ROWS)
    LauncherItemType.WIDGET_LIST -> OnboardingItemSize(WIDGETS_COLS, WIDGETS_ROWS)
    else -> GridDefaults.getDefaultSpanForType(type).first.let { OnboardingItemSize(it.first, it.second) }
}

/**
 * The item for [request] at its compact size: where the default layout puts that type when it is free
 * there, else in the first free slot. Null when the grid has no room (even for a 1x1 item).
 */
internal fun placeOnboardingItem(request: OnboardingPlacementRequest): LauncherItemState? {
    val size = onboardingDefaultSize(request.type)
    val atDefault = GridDefaults.getDefaultItem(request.type, request.isLandscape, request.isLeftHanded)
        ?.copy(id = request.id, colSpan = size.colSpan, rowSpan = size.rowSpan, children = emptyList())
        ?.takeIf { it.isFreeIn(request) }
    if (atDefault != null) return atDefault
    val slot = GridEngineUtils.findFirstAvailableSlot(size.colSpan, size.rowSpan, request.items, request.limits)
    return slot?.let { (col, row) ->
        LauncherItemState(
            id = request.id,
            type = request.type,
            col = col,
            row = row,
            colSpan = size.colSpan,
            rowSpan = size.rowSpan
        )
    }
}

private fun LauncherItemState.isFreeIn(request: OnboardingPlacementRequest): Boolean =
    GridEngineUtils.isWithinBounds(col, row, colSpan, rowSpan, request.limits) &&
        !GridEngineUtils.checkCollisionWithOthers(this, request.items)
