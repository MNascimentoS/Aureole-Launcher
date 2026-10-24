package dev.mnascimentos.aureole.feature.home.components.common

import android.appwidget.AppWidgetHost
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.containers.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.EditFolderDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderAppPickerDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderOverlayUtils
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPopup
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderOverlayParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.components.quickbutton.HomeScreenQuickButtonOverlays
import dev.mnascimentos.aureole.feature.home.components.widgets.EditWidgetGroupScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack

private const val FADE_ANIM_DURATION_MS = 180

@Composable
internal fun HomeScreenFolderBottomSheetOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost? = null,
    hazeState: HazeState? = null
) {
    ContainerAppAndFolderOverlays(uiState = uiState, actions = actions)
    WidgetStackOverlay(uiState = uiState, actions = actions, appWidgetHost = appWidgetHost, hazeState = hazeState)
    HomeScreenQuickButtonOverlays(uiState = uiState, actions = actions, hazeState = hazeState)
}

@Composable
private fun ContainerAppAndFolderOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    uiState.activeContainerAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                onRemoveFromContainer = {
                    actions.onRemoveAppFromContainer(
                        state.panelId,
                        state.app.packageName
                    )
                },
                onOpenContainerSettings = { actions.onOpenEditContainerDialog(state.panelId) }
            ),
            onDismiss = actions.onCloseContainerAppBottomSheet
        )
    }

    uiState.activeContainerFolderBottomSheet?.let { state ->
        ContainerFolderBottomSheet(
            params = ContainerFolderBottomSheetParams(
                onEditShortcuts = {
                    actions.onCloseContainerFolderBottomSheet()
                    actions.onFolderIntent(FolderViewIntent.AddAppToFolder(state.folder.id))
                },
                onRename = {
                    actions.onCloseContainerFolderBottomSheet()
                    actions.onOpenEditFolderForFolder(state.folder)
                },
                onIcon = {
                    actions.onCloseContainerFolderBottomSheet()
                    actions.onOpenEditFolderForFolder(state.folder)
                },
                onRemoveFolder = {
                    actions.onCloseContainerFolderBottomSheet()
                    actions.onFolderIntent(FolderViewIntent.DeleteFolder(state.folder.id))
                },
                onOpenContainerSettings = {
                    actions.onCloseContainerFolderBottomSheet()
                    actions.onOpenEditContainerDialog(state.panelId)
                }
            ),
            onDismiss = actions.onCloseContainerFolderBottomSheet
        )
    }

    uiState.activeFolderAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                onRemoveFromFolder = {
                    actions.onRemoveAppFromFolder(
                        state.folder.id,
                        state.app.packageName
                    )
                },
                onOpenFolderSettings = { actions.onOpenEditFolderForFolder(state.folder) }
            ),
            onDismiss = actions.onCloseFolderAppBottomSheet
        )
    }
}

@Composable
private fun WidgetStackOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost? = null,
    hazeState: HazeState? = null
) {
    uiState.activeWidgetStackBottomSheet?.let { state ->
        val item = LauncherItemState(
            id = state.stackId,
            type = LauncherItemType.WIDGET_LIST,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        EditWidgetGroupScreen(
            params = EditWidgetGroupParams(
                item = item,
                widgets = uiState.getWidgetIdsForStack(state.stackId),
                appWidgetHost = appWidgetHost,
                isIndicatorEnabled = uiState.widgetStackDots[state.stackId] ?: true,
                hazeState = if (uiState.isHazeEnabled) hazeState else null
            ),
            actions = EditWidgetGroupActions(
                onToggleIndicator = { actions.onToggleWidgetStackDots(state.stackId) },
                onRemoveWidget = { actions.onRemoveWidgetClick(it, state.stackId) },
                onAddWidgetClick = { actions.onAddWidgetClick(state.stackId) },
                onDismissRequest = actions.onCloseWidgetStackBottomSheet,
                onReorderWidgets = { actions.onReorderWidgets(it, state.stackId) }
            )
        )
    }
}

@Composable
internal fun CreateFolderOverlay(
    isVisible: Boolean,
    onCloseFolder: () -> Unit,
    onSubmitName: (String, String?) -> Unit
) {
    if (isVisible) {
        CreateFolderDialog(onDismiss = onCloseFolder, onSubmit = onSubmitName)
    }
}

@Composable
internal fun OpenedFolderOverlay(
    uiState: MainUiState,
    screenHeightPx: Float,
    hazeState: HazeState,
    actions: HomeScreenActions
) {
    val density = LocalDensity.current
    var lastFolder by remember { mutableStateOf<AppFolder?>(null) }
    val activeFolder = uiState.activeFolder
    if (activeFolder != null) lastFolder = activeFolder

    val isVisible = activeFolder != null && !uiState.isCreateFolderDialogVisible &&
        !uiState.isRenameFolderDialogVisible && !uiState.isAddAppToFolderDialogVisible
    val folderToDisplay = activeFolder ?: lastFolder

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(FADE_ANIM_DURATION_MS)),
        exit = fadeOut(animationSpec = tween(FADE_ANIM_DURATION_MS))
    ) {
        if (folderToDisplay != null) {
            OpenedFolderOverlayContent(
                params = OpenedFolderOverlayParams(
                    uiState = uiState,
                    folderToDisplay = folderToDisplay,
                    screenHeightPx = screenHeightPx,
                    screenDensity = density.density,
                    hazeState = hazeState
                ),
                actions = actions
            )
        }
    }
}

@Composable
private fun OpenedFolderOverlayContent(
    params: OpenedFolderOverlayParams,
    actions: HomeScreenActions
) {
    val uiState = params.uiState
    val folderToDisplay = params.folderToDisplay
    val isGridFolderEnabled = OpenedFolderOverlayUtils.isGridFolderMode(uiState, folderToDisplay)

    val blurModifier = if (isGridFolderEnabled) {
        Modifier.hazeEffect(
            state = params.hazeState,
            style = HazeStyle(blurRadius = 24.dp, tint = HazeTint(Color.Black.copy(alpha = 0.25f)))
        ) {
            blurEnabled = true
        }
    } else {
        Modifier.Companion
    }
    val overlayBgColor = if (isGridFolderEnabled) Color.Black.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.45f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(blurModifier)
            .background(overlayBgColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    actions.onFolderIntent(
                        FolderViewIntent.CloseFolder
                    )
                })
            }
    ) {
        val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val clampedTopDp = OpenedFolderOverlayUtils.calculateClampedTopDp(
            uiState.activeFolderTopYPx,
            params.screenHeightPx,
            params.screenDensity,
            topInsetDp
        )
        val align = OpenedFolderOverlayUtils.resolvePopupAlignment(
            isGridFolderEnabled,
            uiState.isLeftHandedMode
        )
        val popupPadding = with(OpenedFolderOverlayUtils) {
            Modifier.popupPadding(isGridFolderEnabled, uiState.isLeftHandedMode, clampedTopDp)
        }

        OpenedFolderPopup(
            config = OpenedFolderPopupConfig(
                folder = folderToDisplay,
                allApps = uiState.apps,
                actions = FolderPopupActions(
                    onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                    onAppClick = { app -> actions.onFolderIntent(FolderViewIntent.LaunchApp(app.packageName)) },
                    onAddAppsClick = {
                        actions.onFolderIntent(
                            FolderViewIntent.AddAppToFolder(
                                folderToDisplay.id
                            )
                        )
                    },
                    onEditFolderClick = { actions.onSetRenameFolderDialogVisible(true) }
                ),
                isGridFolderEnabled = isGridFolderEnabled,
                hazeState = if (isGridFolderEnabled) params.hazeState else null
            ),
            modifier = Modifier.align(align).then(popupPadding)
        )
    }
}

@Composable
internal fun AddAppToFolderOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    if (uiState.isAddAppToFolderDialogVisible && uiState.activeFolder != null) {
        FolderAppPickerDialog(
            folder = uiState.activeFolder,
            allApps = uiState.apps,
            onDismiss = { actions.onSetAddAppToFolderDialogVisible(false) },
            onSave = { selectedPackages ->
                actions.onFolderIntent(
                    FolderViewIntent.SaveFolderApps(
                        uiState.activeFolder.id,
                        selectedPackages
                    )
                )
            }
        )
    }
}

@Composable
internal fun RenameFolderOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    if (uiState.isRenameFolderDialogVisible && uiState.activeFolder != null) {
        EditFolderDialog(
            folder = uiState.activeFolder,
            onDismiss = { actions.onSetRenameFolderDialogVisible(false) },
            onSave = { newName, icon ->
                actions.onFolderIntent(
                    FolderViewIntent.RenameFolder(
                        uiState.activeFolder.id,
                        newName,
                        icon
                    )
                )
            },
            onDelete = { actions.onFolderIntent(FolderViewIntent.DeleteFolder(uiState.activeFolder.id)) }
        )
    }
}

@AureolePreview
@Composable
private fun CreateFolderOverlayPreview() {
    AureoleLauncherTheme {
        CreateFolderOverlay(isVisible = true, onCloseFolder = {}, onSubmitName = { _, _ -> })
    }
}
