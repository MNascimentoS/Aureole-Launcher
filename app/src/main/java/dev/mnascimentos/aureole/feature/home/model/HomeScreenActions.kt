package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

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
    val onStartInAppUpdate: () -> Unit = {},
    val onCompleteInAppUpdate: () -> Unit = {},
    val onDismissUpdateDialog: () -> Unit = {},

    // Grid Actions
    val onEnterGridEditMode: () -> Unit = {},
    val onCancelGridEditMode: () -> Unit = {},
    val onSaveGridEditMode: () -> Unit = {},
    val onUpdateGridOrientation: (Int, Int) -> Unit = { _, _ -> },
    val onMoveGridItem: (String, Int, Int) -> Unit = { _, _, _ -> },
    val onResizeGridItem: (String, Int, Int) -> Unit = { _, _, _ -> },
    val onResetGridItems: () -> Unit = {},
    val onOpenAddContainerDialog: () -> Unit = {},
    val onCloseAddContainerDialog: () -> Unit = {},
    val onAddGridItem: (LauncherItemType, Int?) -> Unit = { _, _ -> },
    val onOpenEditContainerDialog: (LauncherItemState) -> Unit = {},
    val onCloseEditContainerDialog: () -> Unit = {},
    val onDeleteGridItem: (String) -> Unit = {},
    val onDismissGridError: () -> Unit = {},
    val onSetIsAddingSingleWidget: (Boolean) -> Unit = {},
)
