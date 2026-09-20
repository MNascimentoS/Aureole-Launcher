package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.data.model.AppInfo

data class HomeScreenActions(
    val onWidgetRowHeightChanged: (Dp) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onRemoveWidgetClick: (Int) -> Unit,
    val onAppClick: (AppInfo) -> Unit,
    val onExpandNotificationShade: () -> Unit,
    val onFolderIntent: (FolderViewIntent) -> Unit,
    val onSetAddAppToFolderDialogVisible: (Boolean) -> Unit,
    val onSetRenameFolderDialogVisible: (Boolean) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onSettingsClick: () -> Unit,
    val onAllAppsDrawerClose: () -> Unit,
    val onAllAppsDrawerOpen: () -> Unit,
    val onToggleFavorite: (String) -> Unit = {},
    val onAppInfoClick: (AppInfo) -> Unit = {},
    val onOpenFavoritePicker: () -> Unit = {},
    val onOpenWidgetPopup: (Int, Float) -> Unit = { _, _ -> },
    val onCloseWidgetPopup: () -> Unit = {},
    val onOpenWidgetResizeDialog: () -> Unit = {},
    val onCloseWidgetResizeDialog: () -> Unit = {},
    val onResizeWidgetHeight: (Dp) -> Unit = {},
)
