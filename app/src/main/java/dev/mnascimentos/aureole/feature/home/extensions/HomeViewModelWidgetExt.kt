package dev.mnascimentos.aureole.feature.home.extensions

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

fun HomeViewModel.openWidgetPopup(widgetId: Int, topYPx: Float) {
    updateUiState {
        it.copy(
            activeWidgetId = widgetId,
            activeWidgetTopYPx = topYPx,
            showWidgetPopup = true
        )
    }
}

fun HomeViewModel.closeWidgetPopup() {
    updateUiState {
        it.copy(
            activeWidgetId = null,
            activeWidgetTopYPx = 0f,
            showWidgetPopup = false,
            showWidgetResizeDialog = false
        )
    }
}

fun HomeViewModel.setShowWidgetResizeDialog(show: Boolean) {
    updateUiState { it.copy(showWidgetResizeDialog = show, showWidgetPopup = false) }
}

fun HomeViewModel.setWidgetRowHeight(height: Dp) {
    updateUiState { it.copy(widgetRowHeight = height) }
    widgetRepository.saveWidgetRowHeight(height.value)
}

fun HomeViewModel.setShowWidgetPicker(show: Boolean) {
    updateUiState { it.copy(showWidgetPicker = show) }
}

fun HomeViewModel.setPendingWidgetId(id: Int) {
    updateUiState { it.copy(pendingWidgetId = id) }
}

fun HomeViewModel.addWidgetId(
    widgetId: Int,
    providerInfo: AppWidgetProviderInfo? = null
) {
    if (uiState.value.isAddingSingleWidget) {
        if (providerInfo != null) {
            val minWidthAdjusted = providerInfo.minWidth + 30
            val targetSpanX = Math.max(1, Math.ceil(minWidthAdjusted / 70.0).toInt())

            val minHeightAdjusted = providerInfo.minHeight + 30
            val targetSpanY = Math.max(1, Math.ceil(minHeightAdjusted / 70.0).toInt())

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
