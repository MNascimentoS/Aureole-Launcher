package dev.mnascimentos.aureole.feature.home.extensions

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel

private const val WIDGET_PADDING_ADJUSTMENT = 30
private const val CELL_SIZE_PX = 70.0

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

fun HomeViewModel.addSingleWidgetGridItem(
    widgetId: Int,
    targetSpanX: Int? = null,
    targetSpanY: Int? = null,
    minSpanX: Int? = null,
    minSpanY: Int? = null
) {
    addGridItem(
        GridItemSpec(
            type = LauncherItemType.SINGLE_APP_WIDGET,
            widgetId = widgetId,
            targetColSpan = targetSpanX,
            targetRowSpan = targetSpanY,
            minColSpan = minSpanX,
            minRowSpan = minSpanY
        )
    )
}

fun HomeViewModel.addWidgetId(
    widgetId: Int,
    providerInfo: AppWidgetProviderInfo? = null
) {
    if (uiState.value.isAddingSingleWidget) {
        if (providerInfo != null) {
            val minWidthAdjusted = providerInfo.minWidth + WIDGET_PADDING_ADJUSTMENT
            val targetSpanX = Math.max(1, Math.ceil(minWidthAdjusted / CELL_SIZE_PX).toInt())

            val minHeightAdjusted = providerInfo.minHeight + WIDGET_PADDING_ADJUSTMENT
            val targetSpanY = Math.max(1, Math.ceil(minHeightAdjusted / CELL_SIZE_PX).toInt())

            addSingleWidgetGridItem(
                widgetId = widgetId,
                targetSpanX = targetSpanX,
                targetSpanY = targetSpanY,
                minSpanX = 1,
                minSpanY = 1
            )
        } else {
            addSingleWidgetGridItem(widgetId)
        }
        setIsAddingSingleWidget(false)
    } else {
        val currentList = uiState.value.topWidgetIds
        if (!currentList.contains(widgetId)) {
            val newList = currentList + widgetId
            updateUiState { it.copy(topWidgetIds = newList) }
            widgetRepository.saveWidgetIds(newList)
        }
    }
}

fun HomeViewModel.removeWidgetId(widgetId: Int) {
    val currentList = uiState.value.topWidgetIds
    val newList = currentList - widgetId
    updateUiState { it.copy(topWidgetIds = newList) }
    widgetRepository.saveWidgetIds(newList)
}
