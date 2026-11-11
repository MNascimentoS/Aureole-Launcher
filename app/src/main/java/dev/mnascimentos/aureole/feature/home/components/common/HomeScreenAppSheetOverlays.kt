package dev.mnascimentos.aureole.feature.home.components.common

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

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
                    onOpenSettings = actions.onSettingsClick
                )
            ),
            onDismiss = actions.onCloseFolderAppBottomSheet
        )
    }
}
