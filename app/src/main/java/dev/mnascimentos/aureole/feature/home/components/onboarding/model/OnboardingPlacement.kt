package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits

/** Columns and rows an item takes on the grid. */
data class OnboardingItemSize(val colSpan: Int, val rowSpan: Int)

/** What it takes to drop one item on the home without an editor: the item, the grid it goes to and the layout. */
data class OnboardingPlacementRequest(
    val id: String,
    val type: LauncherItemType,
    val items: List<LauncherItemState>,
    val limits: GridLimits,
    val isLandscape: Boolean,
    val isLeftHanded: Boolean
)
