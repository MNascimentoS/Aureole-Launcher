package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

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

fun HomeViewModel.setShowWidgetPicker(show: Boolean) {
    updateUiState { it.copy(showWidgetPicker = show) }
}
