package dev.mnascimentos.aureole.feature.home.folder

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
import dev.mnascimentos.aureole.feature.home.components.AppItemBottomSheet
import dev.mnascimentos.aureole.feature.home.components.AppItemBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.ContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.WidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.folder.model.OpenedFolderOverlayParams
import dev.mnascimentos.aureole.feature.home.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val FADE_ANIM_DURATION_MS = 180

@Composable
internal fun HomeScreenBottomSheetOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    ContainerAppOverlay(uiState = uiState, actions = actions)
    ContainerFolderOverlay(uiState = uiState, actions = actions)
    FolderAppOverlay(uiState = uiState, actions = actions)
    WidgetStackOverlay(uiState = uiState, actions = actions)
}

@Composable
private fun ContainerAppOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    uiState.activeContainerAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                onRemoveFromContainer = { actions.onRemoveAppFromContainer(state.panelId, state.app.packageName) },
                onOpenContainerSettings = { actions.onOpenEditContainerDialog(state.panelId) }
            ),
            onDismiss = actions.onCloseContainerAppBottomSheet
        )
    }
}

@Composable
private fun ContainerFolderOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
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
}

@Composable
private fun FolderAppOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    uiState.activeFolderAppBottomSheet?.let { state ->
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = state.app,
                onRemoveFromFolder = { actions.onRemoveAppFromFolder(state.folder.id, state.app.packageName) },
                onOpenFolderSettings = { actions.onOpenEditFolderForFolder(state.folder) }
            ),
            onDismiss = actions.onCloseFolderAppBottomSheet
        )
    }
}

@Composable
private fun WidgetStackOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    uiState.activeWidgetStackBottomSheet?.let { state ->
        WidgetStackBottomSheet(
            params = WidgetStackBottomSheetParams(
                widgetId = state.widgetId,
                stackId = state.stackId,
                onEditStack = { actions.onOpenWidgetResizeDialog() },
                onToggleDots = { sId -> actions.onToggleWidgetStackDots(sId) },
                onRemoveStack = {
                    if (state.widgetId != null && state.widgetId != -1) {
                        actions.onRemoveWidgetClick(state.widgetId)
                    } else {
                        actions.onDeleteGridItem(state.stackId)
                    }
                }
            ),
            onDismiss = actions.onCloseWidgetStackBottomSheet
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
        CreateFolderDialog(
            onDismiss = onCloseFolder,
            onSubmit = onSubmitName
        )
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
    if (activeFolder != null) {
        lastFolder = activeFolder
    }

    val isVisible = activeFolder != null &&
        !uiState.isCreateFolderDialogVisible &&
        !uiState.isRenameFolderDialogVisible &&
        !uiState.isAddAppToFolderDialogVisible
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

    val fullScreenBlurModifier = if (uiState.isHazeEnabled && isGridFolderEnabled) {
        Modifier.hazeEffect(
            state = params.hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(Color.Black.copy(alpha = 0.25f))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val overlayBgColor = if (uiState.isHazeEnabled && isGridFolderEnabled) {
        Color.Black.copy(alpha = 0.2f)
    } else {
        Color.Black.copy(alpha = 0.45f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(fullScreenBlurModifier)
            .background(overlayBgColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { actions.onFolderIntent(FolderViewIntent.CloseFolder) })
            }
    ) {
        val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val clampedTopDp = OpenedFolderOverlayUtils.calculateClampedTopDp(
            topYPx = uiState.activeFolderTopYPx,
            screenHeightPx = params.screenHeightPx,
            screenDensity = params.screenDensity,
            topInsetDp = topInsetDp
        )

        val align = OpenedFolderOverlayUtils.resolvePopupAlignment(isGridFolderEnabled, uiState.isLeftHandedMode)
        val popupPadding = OpenedFolderOverlayUtils.calculatePopupPaddingModifier(
            isGridFolderEnabled = isGridFolderEnabled,
            isLeftHandedMode = uiState.isLeftHandedMode,
            clampedTopDp = clampedTopDp
        )

        OpenedFolderPopup(
            config = OpenedFolderPopupConfig(
                folder = folderToDisplay,
                allApps = uiState.apps,
                actions = FolderPopupActions(
                    onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                    onAppClick = { app -> actions.onFolderIntent(FolderViewIntent.LaunchApp(app.packageName)) },
                    onAddAppsClick = { actions.onFolderIntent(FolderViewIntent.AddAppToFolder(folderToDisplay.id)) },
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
                        folderId = uiState.activeFolder.id,
                        selectedPackageNames = selectedPackages
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
                        folderId = uiState.activeFolder.id,
                        newName = newName,
                        icon = icon
                    )
                )
            },
            onDelete = { actions.onFolderIntent(FolderViewIntent.DeleteFolder(uiState.activeFolder.id)) }
        )
    }
}
