package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.runtime.staticCompositionLocalOf

val LocalHomeUiState = staticCompositionLocalOf { MainUiState() }
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
