package dev.mnascimentos.aureole.feature.home.components.widgets.model

import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val LANDSCAPE_SUFFIX = "_landscape"

/** Key of the position dots of a widget group: portrait keeps the plain id, landscape has its own. */
fun widgetDotsKey(stackId: String, isLandscape: Boolean): String =
    if (isLandscape) "$stackId$LANDSCAPE_SUFFIX" else stackId

/** Whether the dots of [stackId] are on in the current orientation (on by default). */
fun MainUiState.isWidgetDotsEnabled(stackId: String): Boolean =
    widgetStackDots[widgetDotsKey(stackId, isLandscape)] ?: true
