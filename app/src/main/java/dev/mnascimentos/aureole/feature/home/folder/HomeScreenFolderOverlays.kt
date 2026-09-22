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
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180))
    ) {
        if (folderToDisplay != null) {
            val fullScreenBlurModifier = if (uiState.isHazeEnabled) {
                Modifier.hazeEffect(
                    state = hazeState,
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
                val sidePadding = 76.dp
                val screenDensity = density.density
                val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

                val rawTopDp = if (uiState.activeFolderTopYPx > 0f) {
                    (uiState.activeFolderTopYPx / screenDensity).dp - topInsetDp
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
                val clampedTopDp = rawTopDp.coerceIn(8.dp, maxTopDp)

                val panelId = folderToDisplay.panelId
                val activePanel = if (panelId != null) uiState.sidePanels[panelId] else null
                val isGridFolderEnabled = folderToDisplay.displayAsGrid ||
                    (activePanel?.isGridFolderEnabled == true) ||
                    uiState.sidePanels.values.any { it.isGridFolderEnabled }

                val popupAlign = if (isGridFolderEnabled) {
                    Alignment.Center
                } else if (uiState.isLeftHandedMode) {
                    Alignment.TopStart
                } else {
                    Alignment.TopEnd
                }

                val popupModifier = if (isGridFolderEnabled) {
                    Modifier.align(Alignment.Center)
                } else {
                    Modifier
                        .align(popupAlign)
                        .padding(
                            start = if (uiState.isLeftHandedMode) sidePadding else 0.dp,
                            end = if (!uiState.isLeftHandedMode) sidePadding else 0.dp,
                            top = clampedTopDp
                        )
                }

                OpenedFolderPopup(
                    folder = folderToDisplay,
                    allApps = uiState.apps,
                    actions = FolderPopupActions(
                        onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                        onAppClick = { app ->
                            actions.onFolderIntent(FolderViewIntent.LaunchApp(app.packageName))
                        },
                        onAddAppsClick = {
                            actions.onFolderIntent(FolderViewIntent.AddAppToFolder(folderToDisplay.id))
                        },
                        onEditFolderClick = { actions.onSetRenameFolderDialogVisible(true) }
                    ),
                    isGridFolderEnabled = isGridFolderEnabled,
                    hazeState = hazeState,
                    modifier = popupModifier
                )
            }
        }
    }
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
