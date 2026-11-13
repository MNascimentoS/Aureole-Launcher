package dev.mnascimentos.aureole.feature.home.components.widgets.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack

/**
 * Takes [widgetId] out of the stack and offers "Undo", which puts the stack back as it was. The widget keeps its
 * host id until the stack is saved without it, so undoing brings the same widget back. Nothing happens (and no
 * undo is offered) when the widget is not in the stack.
 */
fun HomeViewModel.removeWidgetWithUndo(widgetId: Int, targetStackId: String? = null) {
    val stackId = resolveTargetStackId(targetStackId)
    val previous = currentWidgetIds(stackId)
    if (widgetId !in previous) return
    removeWidgetId(widgetId, stackId)
    showUndo(TextResource.fromResId(R.string.home_undo_widget_removed)) { reorderWidgets(previous, stackId) }
}

/**
 * Saves [newWidgetIds] as the order of the stack and offers "Undo", which restores the previous order. Call it once
 * per gesture (when the drag ends), not at every swap. An unchanged order offers no undo.
 */
fun HomeViewModel.reorderWidgetsWithUndo(newWidgetIds: List<Int>, targetStackId: String? = null) {
    val stackId = resolveTargetStackId(targetStackId)
    val previous = currentWidgetIds(stackId)
    if (previous == newWidgetIds) return
    reorderWidgets(newWidgetIds, stackId)
    showUndo(TextResource.fromResId(R.string.home_undo_widget_order_changed)) { reorderWidgets(previous, stackId) }
}

private fun HomeViewModel.currentWidgetIds(stackId: String): List<Int> = uiState.value.getWidgetIdsForStack(stackId)
