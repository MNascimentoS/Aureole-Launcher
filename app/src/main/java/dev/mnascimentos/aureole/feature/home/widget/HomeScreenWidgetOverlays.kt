package dev.mnascimentos.aureole.feature.home.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.widget.model.OpenedWidgetPopupConfig

@Composable
fun HomeScreenWidgetOverlays(
    hazeState: HazeState
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val density = LocalDensity.current

    if (uiState.showWidgetPopup && uiState.activeWidgetId != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { actions.onCloseWidgetPopup() })
                }
        ) {
            val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
            val sidePadding = 76.dp
            val screenDensity = density.density
            val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

            val rawTopDp = if (uiState.activeWidgetTopYPx > 0f) {
                (uiState.activeWidgetTopYPx / screenDensity).dp - topInsetDp
            } else {
                40.dp
            }
            val clampedTopDp = rawTopDp.coerceIn(8.dp, 500.dp)

            OpenedWidgetPopup(
                config = OpenedWidgetPopupConfig(
                    onDismiss = actions.onCloseWidgetPopup,
                    onResizeClick = actions.onOpenWidgetResizeDialog,
                    onRemoveClick = {
                        actions.onRemoveWidgetClick(uiState.activeWidgetId)
                        actions.onCloseWidgetPopup()
                    },
                    hazeState = hazeState,
                ),
                modifier = Modifier
                    .align(popupAlign)
                    .padding(
                        start = if (uiState.isLeftHandedMode) sidePadding else 0.dp,
                        end = if (!uiState.isLeftHandedMode) sidePadding else 0.dp,
                        top = clampedTopDp
                    )
            )
        }
    }

    if (uiState.showWidgetResizeDialog) {
        WidgetResizeDialog(
            currentHeightDp = uiState.widgetRowHeight,
            onHeightSelected = { newHeight ->
                actions.onResizeWidgetHeight(newHeight)
                actions.onCloseWidgetPopup()
            },
            onDismiss = actions.onCloseWidgetPopup
        )
    }
}
