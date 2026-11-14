package dev.mnascimentos.aureole.feature.home.components.overlays.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker

/** Columns of the "Add to home" grid. */
const val CUSTOMIZE_ADD_COLUMNS = 4

/**
 * What the Customize sheet can put on the home, in the order of the grid.
 *
 * @property labelRes Label under the icon.
 * @property type Grid item type this entry adds; null for the entries that add no grid item: the single widget
 * (opens the widget picker) and the Aureole settings.
 */
enum class CustomizeAddItem(
    @StringRes val labelRes: Int,
    val type: LauncherItemType?
) {
    CLOCK(R.string.home_customize_item_clock, LauncherItemType.CLOCK),
    APPS_LIST(R.string.home_customize_item_apps_list, LauncherItemType.APPS_LIST),
    CONTAINER(R.string.home_customize_item_container, LauncherItemType.SHORTCUTS_CONTAINER),
    WIDGET_GROUP(R.string.home_customize_item_widget_group, LauncherItemType.WIDGET_LIST),
    SCROLL_GROUP(R.string.home_customize_item_scrollview, LauncherItemType.SCROLL_VIEW),
    QUICK_BUTTON(R.string.home_customize_item_quick_button, LauncherItemType.QUICK_BUTTON),
    WIDGET(R.string.home_customize_item_widget, null),
    SETTINGS(R.string.home_customize_item_settings, null);

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

/** What the Customize sheet needs from its host: visibility, how to hide it and the pickers it can open. */
data class CustomizeOverlayParams(
    val isVisible: Boolean,
    val onHide: () -> Unit,
    val onPick: (AppearancePicker) -> Unit
)
