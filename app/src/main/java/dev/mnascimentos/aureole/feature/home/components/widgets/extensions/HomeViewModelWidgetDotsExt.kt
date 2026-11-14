package dev.mnascimentos.aureole.feature.home.components.widgets.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetDotsKey

/** Fills both orientations of a group; landscape inherits the portrait value until it is toggled. */
internal fun HomeViewModel.putDots(dotsMap: MutableMap<String, Boolean>, stackId: String) {
    val portraitKey = widgetDotsKey(stackId, isLandscape = false)
    val landscapeKey = widgetDotsKey(stackId, isLandscape = true)
    dotsMap[portraitKey] = widgetRepository.getWidgetDotsState(portraitKey)
    dotsMap[landscapeKey] = widgetRepository.getWidgetDotsState(landscapeKey, fallbackKey = portraitKey)
}
