package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.components.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateContainerFavorites
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateFavoritePackages
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.overlays.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.dismissReleaseNotes
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetPickerScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPickerScreenParams
import dev.mnascimentos.aureole.feature.home.extensions.toggleShowAllAppsOnHome
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun MainScaffold(
    appWidgetHost: AppWidgetHost?,
    viewModel: HomeViewModel,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        HomeScreen(
            appWidgetHost = appWidgetHost,
            modifier = Modifier.fillMaxSize().padding(padding)
        )

        WidgetPickerOverlay(
            uiState = uiState,
            onDismiss = { viewModel.setShowWidgetPicker(false) },
            onWidgetSelected = onWidgetSelected
        )

        FavoritePickerOverlay(
            uiState = uiState,
            onToggleFavorite = { pkg, containerId -> viewModel.toggleFavorite(pkg, containerId) },
            onUpdateFavorites = { cId, pkgs ->
                if (cId != null) {
                    viewModel.updateContainerFavorites(cId, pkgs)
                } else {
                    viewModel.updateFavoritePackages(pkgs)
                }
            },
            onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
            onDismiss = { viewModel.setShowFavoritePicker(false) }
        )

        InAppUpdateOverlay(
            uiState = uiState,
            actions = actions
        )

        ReleaseNotesOverlay(
            uiState = uiState,
            onDismiss = { viewModel.dismissReleaseNotes() }
        )
    }
}

@Composable
private fun WidgetPickerOverlay(
    uiState: MainUiState,
    onDismiss: () -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    if (uiState.showWidgetPicker) {
        WidgetPickerScreen(
            params = WidgetPickerScreenParams(
                isBlurEnabled = uiState.isHazeEnabled,
                hazeState = LocalHazeState.current,
                onWidgetSelected = { provider ->
                    onDismiss()
                    onWidgetSelected(provider)
                },
                onDismiss = onDismiss
            )
        )
    }
}

@Composable
private fun FavoritePickerOverlay(
    uiState: MainUiState,
    onToggleFavorite: (String, String?) -> Unit,
    onUpdateFavorites: (String?, List<String>) -> Unit,
    onToggleShowAllAppsOnHome: () -> Unit,
    onDismiss: () -> Unit
) {
    if (uiState.showFavoritePickerDialog) {
        val containerId = uiState.activeFavoriteContainerId
        val favPackages = if (containerId != null && uiState.containerFavorites.containsKey(containerId)) {
            uiState.containerFavorites[containerId] ?: uiState.favoriteAppPackages
        } else {
            uiState.favoriteAppPackages
        }

        FavoriteAppsDialog(
            config = FavoriteAppsDialogConfig(
                allApps = uiState.apps,
                favoriteAppPackages = favPackages,
                containerId = containerId,
                showAllAppsOnHome = uiState.showAllAppsOnHome
            ),
            actions = FavoriteAppsDialogActions(
                onToggleFavorite = { pkg -> onToggleFavorite(pkg, containerId) },
                onUpdateFavoritePackages = { cId, pkgs -> onUpdateFavorites(cId, pkgs) },
                onToggleShowAllAppsOnHome = onToggleShowAllAppsOnHome,
                onDismiss = onDismiss
            )
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

@Composable
private fun ReleaseNotesOverlay(
    uiState: MainUiState,
    onDismiss: () -> Unit
) {
    if (uiState.showReleaseNotesBottomSheet) {
        ReleaseNotesBottomSheet(
            onDismissRequest = onDismiss
        )
    }
}
