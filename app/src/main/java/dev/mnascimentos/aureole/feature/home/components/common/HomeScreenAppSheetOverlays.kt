package dev.mnascimentos.aureole.feature.home.components.common

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.ContainerAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.FolderAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState

@Composable
internal fun ContainerAppSheetOverlay(uiState: MainUiState, actions: HomeScreenActions) {
    uiState.activeContainerAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                isFavorite = state.app.packageName in
                    (uiState.containerFavorites[state.panelId] ?: uiState.favoriteAppPackages),
                onRemoveFromContainer = { actions.onRemoveAppFromContainer(state.panelId, state.app.packageName) },
                onOpenContainerSettings = { actions.onOpenEditContainerDialog(state.panelId) },
                actions = AppItemRowActions(
                    onToggleFavorite = { pkg -> actions.onToggleFavoriteWithContainer(pkg, state.panelId) },
                    onEditFavoritesClick = { actions.onOpenFavoritePicker(state.panelId) },
                    onOpenSettings = actions.onSettingsClick
                )
            ),
            onDismiss = actions.onCloseContainerAppBottomSheet
        )
    }
}

@Composable
internal fun FolderAppSheetOverlay(uiState: MainUiState, actions: HomeScreenActions) {
    uiState.activeFolderAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                isFavorite = state.app.packageName in uiState.favoriteAppPackages,
                onRemoveFromFolder = { actions.onRemoveAppFromFolder(state.folder.id, state.app.packageName) },
                onOpenFolderSettings = { actions.onOpenEditFolderForFolder(state.folder) },
                actions = AppItemRowActions(
                    onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) },
                    onEditFavoritesClick = { actions.onOpenFavoritePicker(null) },
                    onOpenSettings = actions.onSettingsClick
                )
            ),
            onDismiss = actions.onCloseFolderAppBottomSheet
        )
    }
}

@AureolePreview
@Composable
private fun ContainerAppSheetOverlayPreview() {
    val app = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val uiState = MainUiState(
        activeContainerAppBottomSheet = ContainerAppBottomSheetState(app = app, panelId = "1")
    )
    AureoleLauncherTheme {
        ProvideHomeUiState(uiState) {
            ContainerAppSheetOverlay(
                uiState = uiState,
                actions = HomeScreenActions(
                    onWidgetRowHeightChanged = {},
                    onAppClick = {},
                    onExpandNotificationShade = {},
                    onFolderIntent = {},
                    onSetAddAppToFolderDialogVisible = {},
                    onSetRenameFolderDialogVisible = {},
                    onSearchQueryChanged = {},
                    onSettingsClick = {},
                    onAllAppsDrawerClose = {},
                    onAllAppsDrawerOpen = {}
                )
            )
        }
    }
}

@AureolePreview
@Composable
private fun FolderAppSheetOverlayPreview() {
    val app = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val folder = AppFolder(id = "1", name = "Media", appPackageNames = listOf(app.packageName))
    val uiState = MainUiState(
        activeFolderAppBottomSheet = FolderAppBottomSheetState(app = app, folder = folder)
    )
    AureoleLauncherTheme {
        ProvideHomeUiState(uiState) {
            FolderAppSheetOverlay(
                uiState = uiState,
                actions = HomeScreenActions(
                    onWidgetRowHeightChanged = {},
                    onAppClick = {},
                    onExpandNotificationShade = {},
                    onFolderIntent = {},
                    onSetAddAppToFolderDialogVisible = {},
                    onSetRenameFolderDialogVisible = {},
                    onSearchQueryChanged = {},
                    onSettingsClick = {},
                    onAllAppsDrawerClose = {},
                    onAllAppsDrawerOpen = {}
                )
            )
        }
    }
}
