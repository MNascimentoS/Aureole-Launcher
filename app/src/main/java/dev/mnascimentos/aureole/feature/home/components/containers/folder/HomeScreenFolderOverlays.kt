package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.appwidget.AppWidgetHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

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

@AureolePreview
@Composable
private fun HomeScreenFolderOverlaysPreview() {
    val hazeState = remember { HazeState() }
    val mockActions = HomeScreenActions(
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
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalHomeActions provides mockActions
        ) {
            HomeScreenFolderOverlays(
                screenHeightPx = 1920f,
                hazeState = hazeState
            )
        }
    }
}
