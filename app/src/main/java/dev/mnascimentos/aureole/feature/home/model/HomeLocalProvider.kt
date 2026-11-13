package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded

val LocalHomeUiState = staticCompositionLocalOf { MainUiState() }

/** Provides [uiState] and, with it, the hand mode that the design system and `composable/` read. */
@Composable
fun ProvideHomeUiState(uiState: MainUiState, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalHomeUiState provides uiState,
        LocalLeftHanded provides uiState.isLeftHandedMode,
        content = content
    )
}

val LocalHomeActions = staticCompositionLocalOf { createDefaultHomeScreenActions() }

fun createDefaultHomeScreenActions(): HomeScreenActions = HomeScreenActions(
    onWidgetRowHeightChanged = {},
    onAppClick = {},
    onExpandNotificationShade = {},
    onFolderIntent = {},
    onSetAddAppToFolderDialogVisible = {},
    onSetRenameFolderDialogVisible = {},
    onSearchQueryChanged = {},
    onSettingsClick = {},
    onAllAppsDrawerClose = {},
    onAllAppsDrawerOpen = {},
)
