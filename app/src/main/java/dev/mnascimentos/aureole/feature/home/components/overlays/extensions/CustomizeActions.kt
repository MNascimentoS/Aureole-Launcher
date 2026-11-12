package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

/** Runs what tapping [item] in the "Add to home" grid does; the caller closes the sheet afterwards. */
fun HomeScreenActions.addCustomizeItem(item: CustomizeAddItem) {
    when (item) {
        CustomizeAddItem.CLOCK -> onAddClock()
        CustomizeAddItem.FAVORITES -> onAddFavoritesList()
        CustomizeAddItem.WIDGET -> {
            onSetIsAddingSingleWidget(true)
            onAddWidgetClick(null)
        }
        CustomizeAddItem.SCROLL_GROUP -> onAddGridItem(item.type ?: return, null, ScrollOrientation.VERTICAL)
        else -> onAddGridItem(item.type ?: return, null, null)
    }
}
