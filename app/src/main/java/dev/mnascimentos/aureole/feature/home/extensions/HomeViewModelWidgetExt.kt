package dev.mnascimentos.aureole.feature.home.extensions

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel

internal fun HomeViewModel.loadWidgetSettings() {
    val savedIds = widgetRepository.getSavedWidgetIds()
    val savedHeight = widgetRepository.getSavedWidgetRowHeight()
    updateUiState {
        it.copy(
            topWidgetIds = savedIds,
            widgetRowHeight = savedHeight.dp,
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

fun HomeViewModel.addWidgetId(
    widgetId: Int
) {
    val currentList = uiState.value.topWidgetIds
    if (!currentList.contains(widgetId)) {
        val newList = currentList + widgetId
        updateUiState { it.copy(topWidgetIds = newList) }
        widgetRepository.saveWidgetIds(newList)
    }

    val hasWidgetContainer = uiState.value.gridItems.any {
        it.safeType == LauncherItemType.WIDGET_LIST || it.safeType == LauncherItemType.SINGLE_APP_WIDGET
    }
    if (!hasWidgetContainer) {
        addGridItem(GridItemSpec(type = LauncherItemType.WIDGET_LIST))
    }
    setIsAddingSingleWidget(false)
}

fun HomeViewModel.removeWidgetId(widgetId: Int) {
    val currentList = uiState.value.topWidgetIds
    val newList = currentList - widgetId
    updateUiState { it.copy(topWidgetIds = newList) }
    widgetRepository.saveWidgetIds(newList)
}
