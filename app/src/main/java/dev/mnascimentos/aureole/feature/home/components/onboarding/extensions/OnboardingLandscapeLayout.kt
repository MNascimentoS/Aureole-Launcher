package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults

/**
 * The landscape home saved when the onboarding ends: the default landscape layout (mirrored for left-handed),
 * with the apps list showing all apps. An item the user built in [portraitItems] keeps its id, so the clock
 * config, the favorites and the container items are shared, and its widget stack and settings come along;
 * only the position and size are the default ones. With nothing in portrait, the layout is the default as is.
 */
internal fun onboardingLandscapeLayout(
    portraitItems: List<LauncherItemState>,
    isLeftHanded: Boolean
): List<LauncherItemState> =
    GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = isLeftHanded).map { slot ->
        val built = portraitItems.firstOrNull { it.safeType == slot.safeType } ?: return@map slot
        built.copy(
            col = slot.col,
            row = slot.row,
            colSpan = slot.colSpan,
            rowSpan = slot.rowSpan,
            minColSpan = slot.minColSpan,
            minRowSpan = slot.minRowSpan,
            showAllApps = if (slot.safeType == LauncherItemType.APPS_LIST) true else built.showAllApps
        )
    }
