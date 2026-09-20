package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
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
    if (uiState.activeFolder != null && !uiState.isCreateFolderDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { actions.onFolderIntent(FolderViewIntent.CloseFolder) })
                }
        ) {
            val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
            val sidePadding = 76.dp

            val screenDensity = density.density
            val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

            val rawTopDp = if (uiState.activeFolderTopYPx > 0f) {
                (uiState.activeFolderTopYPx / screenDensity).dp - topInsetDp
            } else {
                40.dp
            }
            val maxTopDp = if (screenHeightPx > 0f) {
                ((screenHeightPx / screenDensity) - POPUP_MAX_OFFSET_SUBTRAHEND).coerceAtLeast(POPUP_MIN_OFFSET_DP).dp
            } else {
                280.dp
            }
            val clampedTopDp = rawTopDp.coerceIn(8.dp, maxTopDp)

            OpenedFolderPopup(
                folder = uiState.activeFolder,
                allApps = uiState.apps,
                actions = FolderPopupActions(
                    onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                    onAppClick = { app ->
                        actions.onFolderIntent(FolderViewIntent.LaunchApp(app.packageName))
                    },
                    onAddAppsClick = {
                        actions.onFolderIntent(FolderViewIntent.AddAppToFolder(uiState.activeFolder.id))
                    },
                    onEditFolderClick = { actions.onSetRenameFolderDialogVisible(true) }
                ),
                hazeState = hazeState,
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
