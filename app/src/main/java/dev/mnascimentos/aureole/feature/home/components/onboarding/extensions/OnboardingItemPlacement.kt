package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingItemSize
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPlacementRequest
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest

private const val CLOCK_COLS = 10
private const val CLOCK_ROWS = 4
private const val CONTAINER_COLS = 4
private const val CONTAINER_ROWS = 6

/**
 * The size an onboarding item gets until the layout step, so no item takes the whole home: in portrait a clock
 * 10x4 and a container 4x6 cells (a widget list keeps its default); in landscape the smallest the type accepts.
 * Other types keep their default size.
 */
internal fun onboardingDefaultSize(type: LauncherItemType, isLandscape: Boolean = false): OnboardingItemSize {
    val (default, minimum) = GridDefaults.getDefaultSpanForType(type)
    return when {
        isLandscape && type in COMPACT_TYPES -> OnboardingItemSize(minimum.first, minimum.second)
        type == LauncherItemType.CLOCK -> OnboardingItemSize(CLOCK_COLS, CLOCK_ROWS)
        type == LauncherItemType.SHORTCUTS_CONTAINER -> OnboardingItemSize(CONTAINER_COLS, CONTAINER_ROWS)
        else -> OnboardingItemSize(default.first, default.second)
    }
}

private val COMPACT_TYPES = setOf(
    LauncherItemType.CLOCK,
    LauncherItemType.SHORTCUTS_CONTAINER,
    LauncherItemType.WIDGET_LIST
)

/**
 * The item for [request] at its onboarding size: where the default layout puts that type when it is free
 * there, else in the first free slot; when that size fits nowhere, the largest size that fits (never below the
 * type's minimum). Null when the grid has no room (even for a 1x1 item).
 */
internal fun placeOnboardingItem(request: OnboardingPlacementRequest): LauncherItemState? {
    val size = onboardingDefaultSize(request.type, request.isLandscape)
    val atDefault = GridDefaults.getDefaultItem(request.type, request.isLandscape, request.isLeftHanded)
        ?.copy(id = request.id, colSpan = size.colSpan, rowSpan = size.rowSpan, children = emptyList())
        ?.takeIf { it.isFreeIn(request) }
    return atDefault
        ?: request.firstFreeItem(size)
        ?: request.largestFreeItem(size)
}

private fun OnboardingPlacementRequest.firstFreeItem(size: OnboardingItemSize): LauncherItemState? =
    GridEngineUtils.findFirstAvailableSlot(size.colSpan, size.rowSpan, items, limits)
        ?.let { (col, row) -> newItem(size, col, row) }

private fun OnboardingPlacementRequest.largestFreeItem(size: OnboardingItemSize): LauncherItemState? {
    val minimum = GridDefaults.getDefaultSpanForType(type).second
    val search = SlotSearchRequest(
        targetColSpan = size.colSpan,
        targetRowSpan = size.rowSpan,
        minColSpan = minimum.first,
        minRowSpan = minimum.second,
        limits = limits
    )
    return GridEngineUtils.findLargestAvailableSlot(search, items)?.let { (colSpan, rowSpan, slot) ->
        newItem(OnboardingItemSize(colSpan, rowSpan), slot.first, slot.second)
    }
}

private fun OnboardingPlacementRequest.newItem(size: OnboardingItemSize, col: Int, row: Int) = LauncherItemState(
    id = id,
    type = type,
    col = col,
    row = row,
    colSpan = size.colSpan,
    rowSpan = size.rowSpan
)

private fun LauncherItemState.isFreeIn(request: OnboardingPlacementRequest): Boolean =
    GridEngineUtils.isWithinBounds(col, row, colSpan, rowSpan, request.limits) &&
        !GridEngineUtils.checkCollisionWithOthers(this, request.items)
