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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val POPUP_MAX_OFFSET_SUBTRAHEND = 300
private const val POPUP_MIN_OFFSET_DP = 16f
private const val FADE_ANIM_DURATION_MS = 180
private val SIDE_PADDING_DP = 76.dp

data class OpenedFolderOverlayParams(
    val uiState: MainUiState,
    val folderToDisplay: AppFolder,
    val screenHeightPx: Float,
    val screenDensity: Float,
    val hazeState: HazeState
)

@Composable
fun HomeScreenFolderOverlays(
    screenHeightPx: Float,
    hazeState: HazeState
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    CreateFolderOverlay(
        isVisible = uiState.isCreateFolderDialogVisible,
        onCloseFolder = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
        onSubmitName = { folderName -> actions.onFolderIntent(FolderViewIntent.SubmitFolderName(folderName)) }
    )

    OpenedFolderOverlay(
        uiState = uiState,
        screenHeightPx = screenHeightPx,
        hazeState = hazeState,
        actions = actions
    )

    AddAppToFolderOverlay(
        uiState = uiState,
        actions = actions
    )

    RenameFolderOverlay(
        uiState = uiState,
        actions = actions
    )
}

@Composable
private fun CreateFolderOverlay(
    isVisible: Boolean,
    onCloseFolder: () -> Unit,
    onSubmitName: (String) -> Unit
) {
    if (isVisible) {
        CreateFolderDialog(
            onDismiss = onCloseFolder,
            onSubmit = onSubmitName
        )
    }
}

@Composable
private fun OpenedFolderOverlay(
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

    val isVisible = activeFolder != null && !uiState.isCreateFolderDialogVisible
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

    val fullScreenBlurModifier = if (uiState.isHazeEnabled) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(fullScreenBlurModifier)
            .background(Color.Black.copy(alpha = 0.2f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { actions.onFolderIntent(FolderViewIntent.CloseFolder) })
            }
    ) {
        val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val clampedTopDp = calculateClampedTopDp(
            topYPx = uiState.activeFolderTopYPx,
            screenHeightPx = params.screenHeightPx,
            screenDensity = params.screenDensity,
            topInsetDp = topInsetDp
        )

        val isGridFolderEnabled = isGridFolderMode(uiState, folderToDisplay)
        val align = resolvePopupAlignment(isGridFolderEnabled, uiState.isLeftHandedMode)
        val popupPadding = calculatePopupPaddingModifier(isGridFolderEnabled, uiState.isLeftHandedMode, clampedTopDp)

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
                hazeState = params.hazeState
            ),
            modifier = Modifier.align(align).then(popupPadding)
        )
    }
}

private fun resolvePopupAlignment(isGridFolderEnabled: Boolean, isLeftHandedMode: Boolean): Alignment {
    return if (isGridFolderEnabled) {
        Alignment.Center
    } else if (isLeftHandedMode) {
        Alignment.TopStart
    } else {
        Alignment.TopEnd
    }
}

private fun calculatePopupPaddingModifier(
    isGridFolderEnabled: Boolean,
    isLeftHandedMode: Boolean,
    clampedTopDp: Dp
): Modifier {
    return if (isGridFolderEnabled) {
        Modifier
    } else {
        Modifier.padding(
            start = if (isLeftHandedMode) SIDE_PADDING_DP else 0.dp,
            end = if (!isLeftHandedMode) SIDE_PADDING_DP else 0.dp,
            top = clampedTopDp
        )
    }
}

private fun calculateClampedTopDp(
    topYPx: Float,
    screenHeightPx: Float,
    screenDensity: Float,
    topInsetDp: Dp
): Dp {
    val rawTopDp = if (topYPx > 0f) {
        (topYPx / screenDensity).dp - topInsetDp
    } else {
        40.dp
    }
    val maxTopDp = if (screenHeightPx > 0f) {
        ((screenHeightPx / screenDensity) - POPUP_MAX_OFFSET_SUBTRAHEND).coerceAtLeast(
            POPUP_MIN_OFFSET_DP
        ).dp
    } else {
        280.dp
    }
    return rawTopDp.coerceIn(8.dp, maxTopDp)
}

private fun isGridFolderMode(uiState: MainUiState, folder: AppFolder): Boolean {
    val panelId = folder.panelId
    val activePanel = if (panelId != null) uiState.sidePanels[panelId] else null
    return folder.displayAsGrid ||
        (activePanel?.isGridFolderEnabled == true) ||
        uiState.sidePanels.values.any { it.isGridFolderEnabled }
}

@Composable
private fun AddAppToFolderOverlay(
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
private fun RenameFolderOverlay(
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
