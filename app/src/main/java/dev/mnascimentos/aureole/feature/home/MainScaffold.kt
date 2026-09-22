package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.LocalHazeState
import dev.mnascimentos.aureole.feature.home.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.feature.home.components.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.OpenedWidgetPopup
import dev.mnascimentos.aureole.feature.home.widget.WidgetPickerBottomSheet
import dev.mnascimentos.aureole.feature.home.widget.WidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.widget.model.OpenedWidgetPopupConfig

@Composable
fun MainScaffold(
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    viewModel: HomeViewModel,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onRemoveWidget: (Int) -> Unit
) {
    val uiState = LocalHomeUiState.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        HomeScreen(
            appWidgetHost = appWidgetHost,
            modifier = Modifier.fillMaxSize().padding(padding)
        )

        WidgetPickerOverlay(
            uiState = uiState,
            appWidgetManager = appWidgetManager,
            viewModel = viewModel,
            onWidgetSelected = onWidgetSelected
        )

        FavoritePickerOverlay(
            uiState = uiState,
            viewModel = viewModel
        )

        WidgetPopupOverlay(
            uiState = uiState,
            viewModel = viewModel,
            onRemoveWidget = onRemoveWidget
        )

        WidgetResizeOverlay(
            uiState = uiState,
            viewModel = viewModel
        )

        InAppUpdateOverlay(
            uiState = uiState,
            actions = LocalHomeActions.current
        )
    }
}

@Composable
private fun WidgetPickerOverlay(
    uiState: MainUiState,
    appWidgetManager: AppWidgetManager,
    viewModel: HomeViewModel,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    if (uiState.showWidgetPicker) {
        WidgetPickerBottomSheet(
            appWidgetManager = appWidgetManager,
            onWidgetSelected = { provider ->
                viewModel.setShowWidgetPicker(false)
                onWidgetSelected(provider)
            },
            onDismiss = { viewModel.setShowWidgetPicker(false) }
        )
    }
}

@Composable
private fun FavoritePickerOverlay(
    uiState: MainUiState,
    viewModel: HomeViewModel
) {
    if (uiState.showFavoritePickerDialog) {
        FavoriteAppsDialog(
            allApps = uiState.apps,
            favoriteAppPackages = uiState.favoriteAppPackages,
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onDismiss = { viewModel.setShowFavoritePicker(false) }
        )
    }
}

@Composable
private fun WidgetPopupOverlay(
    uiState: MainUiState,
    viewModel: HomeViewModel,
    onRemoveWidget: (Int) -> Unit
) {
    if (uiState.showWidgetPopup && uiState.activeWidgetId != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { viewModel.closeWidgetPopup() })
                }
        ) {
            val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
            val sidePadding = 76.dp
            val screenDensity = LocalDensity.current.density
            val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

            val rawTopDp = if (uiState.activeWidgetTopYPx > 0f) {
                (uiState.activeWidgetTopYPx / screenDensity).dp - topInsetDp
            } else {
                40.dp
            }
            val clampedTopDp = rawTopDp.coerceIn(8.dp, 500.dp)

            OpenedWidgetPopup(
                config = OpenedWidgetPopupConfig(
                    onDismiss = { viewModel.closeWidgetPopup() },
                    onResizeClick = { viewModel.setShowWidgetResizeDialog(true) },
                    onRemoveClick = {
                        onRemoveWidget(uiState.activeWidgetId)
                        viewModel.closeWidgetPopup()
                    },
                    hazeState = LocalHazeState.current,
                    isHazeEnabled = uiState.isHazeEnabled,
                    hazeOpacity = uiState.hazeOpacity,
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
}

@Composable
private fun WidgetResizeOverlay(
    uiState: MainUiState,
    viewModel: HomeViewModel
) {
    if (uiState.showWidgetResizeDialog) {
        WidgetResizeDialog(
            currentHeightDp = uiState.widgetRowHeight,
            onHeightSelected = { newHeight ->
                viewModel.setWidgetRowHeight(newHeight)
                viewModel.closeWidgetPopup()
            },
            onDismiss = { viewModel.closeWidgetPopup() }
        )
    }
}

@Composable
private fun InAppUpdateOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    if (uiState.showUpdateAvailableDialog) {
        UpdateAvailableDialog(
            onConfirmUpdate = actions.onStartInAppUpdate,
            onDismiss = actions.onDismissUpdateDialog
        )
    }

    if (uiState.showUpdateDownloadedDialog) {
        UpdateDownloadedDialog(
            onConfirmRestart = actions.onCompleteInAppUpdate,
            onDismiss = actions.onDismissUpdateDialog
        )
    }
}
