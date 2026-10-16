package dev.mnascimentos.aureole.feature.home.components.widgets.extensions

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.extensions.GridItemSpec
import dev.mnascimentos.aureole.feature.home.grid.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.dismissGridError
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack

fun HomeViewModel.loadWidgetSettings() {
    val topIds = widgetRepository.getWidgetIdsForStack("top_widgets")
    val savedHeight = widgetRepository.getSavedWidgetRowHeight()
    val topDots = widgetRepository.getWidgetDotsState("top_widgets")

    val widgetMap = mutableMapOf<String, List<Int>>()
    widgetMap["top_widgets"] = topIds

    val dotsMap = mutableMapOf<String, Boolean>()
    dotsMap["top_widgets"] = topDots

    val allItems = uiState.value.portraitGridItems + uiState.value.landscapeGridItems
    fun processItem(item: LauncherItemState) {
        val isWidgetType = item.safeType == LauncherItemType.WIDGET_LIST ||
            item.safeType == LauncherItemType.SINGLE_APP_WIDGET
        if (isWidgetType) {
            val stackId = item.safeId
            widgetMap[stackId] = widgetRepository.getWidgetIdsForStack(stackId)
            dotsMap[stackId] = widgetRepository.getWidgetDotsState(stackId)
        }
        item.safeChildren.forEach { child -> processItem(child) }
    }
    allItems.forEach { item -> processItem(item) }

    updateUiState {
        it.copy(
            topWidgetIds = topIds,
            widgetStacks = widgetMap,
            widgetRowHeight = savedHeight.dp,
            widgetStackDots = dotsMap
        )
    }
}

fun HomeViewModel.setWidgetRowHeight(height: Dp) {
    updateUiState { it.copy(widgetRowHeight = height) }
    widgetRepository.saveWidgetRowHeight(height.value)
}

fun HomeViewModel.setPendingWidgetId(id: Int) {
    updateUiState { it.copy(pendingWidgetId = id) }
}

fun HomeViewModel.setIsAddingSingleWidget(isAdding: Boolean) {
    updateUiState { it.copy(isAddingSingleWidget = isAdding) }
}

fun HomeViewModel.setTargetWidgetStackId(stackId: String?) {
    updateUiState { it.copy(targetWidgetStackId = stackId) }
}

private fun HomeViewModel.resolveTargetStackId(targetStackId: String?): String {
    return targetStackId
        ?: uiState.value.targetWidgetStackId
        ?: uiState.value.activeWidgetStackBottomSheet?.stackId
        ?: uiState.value.editingGridItem?.id
        ?: "top_widgets"
}

fun HomeViewModel.addWidgetId(
    widgetId: Int,
    targetStackId: String? = null
) {
    val stackId = resolveTargetStackId(targetStackId)
    val currentMap = uiState.value.widgetStacks
    val currentList = currentMap[stackId] ?: uiState.value.getWidgetIdsForStack(stackId)
    if (!currentList.contains(widgetId)) {
        val newList = currentList + widgetId
        val updatedMap = currentMap + (stackId to newList)
        updateUiState {
            it.copy(
                widgetStacks = updatedMap,
                topWidgetIds = if (stackId == "top_widgets") newList else it.topWidgetIds,
                targetWidgetStackId = null
            )
        }
        widgetRepository.saveWidgetIdsForStack(stackId, newList)
    }

    val hasWidgetContainer = uiState.value.gridItems.any { item ->
        val isSelfWidget = item.safeType == LauncherItemType.WIDGET_LIST ||
            item.safeType == LauncherItemType.SINGLE_APP_WIDGET
        val hasChildWidget = item.safeChildren.any { child ->
            child.safeType == LauncherItemType.WIDGET_LIST ||
                child.safeType == LauncherItemType.SINGLE_APP_WIDGET
        }
        isSelfWidget || hasChildWidget
    }
    if (!hasWidgetContainer) {
        addGridItem(GridItemSpec(type = LauncherItemType.WIDGET_LIST))
        dismissGridError()
    }
    setIsAddingSingleWidget(false)
}

fun HomeViewModel.removeWidgetId(
    widgetId: Int,
    targetStackId: String? = null
) {
    val stackId = resolveTargetStackId(targetStackId)
    val currentMap = uiState.value.widgetStacks
    val currentList = currentMap[stackId] ?: uiState.value.getWidgetIdsForStack(stackId)
    val newList = currentList - widgetId
    val updatedMap = currentMap + (stackId to newList)
    updateUiState {
        it.copy(
            widgetStacks = updatedMap,
            topWidgetIds = if (stackId == "top_widgets") newList else it.topWidgetIds
        )
    }
    widgetRepository.saveWidgetIdsForStack(stackId, newList)
}
