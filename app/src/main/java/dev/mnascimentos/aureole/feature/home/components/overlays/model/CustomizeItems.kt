package dev.mnascimentos.aureole.feature.home.components.overlays.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker

/** Columns of the "Add to home" grid. */
const val CUSTOMIZE_ADD_COLUMNS = 4

/**
 * What the Customize sheet can put on the home, in the order of the grid.
 *
 * @property labelRes Label under the icon.
 * @property type Grid item type this entry adds; null for the single widget, which opens the widget picker.
 * @property isSingleton True for items a home keeps only once (all apps): the grid marks the ones already
 * there. The other kinds can be added any number of times.
 */
enum class CustomizeAddItem(
    @StringRes val labelRes: Int,
    val type: LauncherItemType?,
    val isSingleton: Boolean = false
) {
    CLOCK(R.string.home_customize_item_clock, LauncherItemType.CLOCK),
    FAVORITES(R.string.home_customize_item_favorites, LauncherItemType.APPS_LIST),
    ALL_APPS(R.string.home_customize_item_all_apps, LauncherItemType.ALL_APPS_LIST, isSingleton = true),
    CONTAINER(R.string.home_customize_item_container, LauncherItemType.SHORTCUTS_CONTAINER),
    WIDGET_GROUP(R.string.home_customize_item_widget_group, LauncherItemType.WIDGET_LIST),
    SCROLL_GROUP(R.string.home_customize_item_scrollview, LauncherItemType.SCROLL_VIEW),
    QUICK_BUTTON(R.string.home_customize_item_quick_button, LauncherItemType.QUICK_BUTTON),
    WIDGET(R.string.home_customize_item_widget, null);

    companion object {
        /** The items split into grid rows of [CUSTOMIZE_ADD_COLUMNS]. */
        val rows: List<List<CustomizeAddItem>> = entries.chunked(CUSTOMIZE_ADD_COLUMNS)
    }
}

/** The "Adjust" buttons of the sheet, in order. */
enum class CustomizeAdjustItem(@StringRes val labelRes: Int) {
    LAYOUT(R.string.home_customize_adjust_layout),
    PALETTE(R.string.home_customize_adjust_palette),
    FONT(R.string.home_customize_adjust_font)
}

/** True when [item] is a once-only kind that [gridItems] already holds at the top level of the home. */
fun CustomizeAddItem.isOnHome(gridItems: List<LauncherItemState>): Boolean =
    isSingleton && gridItems.any { it.safeType == type }

/** The items of the grid that are already on the home, for the dimmed state with a check mark. */
fun itemsOnHome(gridItems: List<LauncherItemState>): Set<CustomizeAddItem> =
    CustomizeAddItem.entries.filterTo(mutableSetOf()) { it.isOnHome(gridItems) }

/** What the Customize sheet needs from its host: visibility, how to hide it and the pickers it can open. */
data class CustomizeOverlayParams(
    val isVisible: Boolean,
    val onHide: () -> Unit,
    val onPick: (AppearancePicker) -> Unit
)
