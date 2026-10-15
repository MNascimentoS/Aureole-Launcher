package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.appwidget.AppWidgetHost
import androidx.compose.runtime.Composable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent

@Composable
fun HomeScreenFolderOverlays(
    screenHeightPx: Float,
    hazeState: HazeState,
    appWidgetHost: AppWidgetHost? = null
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    CreateFolderOverlay(
        isVisible = uiState.isCreateFolderDialogVisible,
        onCloseFolder = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
        onSubmitName = { folderName, icon ->
            actions.onFolderIntent(
                FolderViewIntent.SubmitFolderName(folderName, icon)
            )
        }
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

    HomeScreenBottomSheetOverlays(
        uiState = uiState,
        actions = actions,
        appWidgetHost = appWidgetHost,
        hazeState = hazeState
    )
}
