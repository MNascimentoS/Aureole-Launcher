package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.model.OpenedWidgetPopupConfig
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig

private const val PREVIEW_APPWIDGET_HOST_ID = 1024
private const val PREVIEW_HEIGHT_PX = 400f

@AureolePreview
@Composable
fun StackedWidgetSectionPreview() {
    val context = LocalContext.current
    val mockUiState = MainUiState()
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = {},
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
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides mockActions
        ) {
            StackedWidgetSection(
                config = StackedWidgetConfig(
                    topWidgetIds = emptyList(),
                    currentHeightDp = 160.dp,
                    currentHeightPx = PREVIEW_HEIGHT_PX
                ),
                appWidgetHost = remember { AppWidgetHost(context, PREVIEW_APPWIDGET_HOST_ID) }
            )
        }
    }
}

@AureolePreview
@Composable
fun WidgetResizeDialogPreview() {
    AureoleLauncherTheme {
        WidgetResizeDialog(
            currentHeightDp = 160.dp,
            onHeightSelected = {},
            onDismiss = {}
        )
    }
}

@AureolePreview
@Composable
fun OpenedWidgetPopupPreview() {
    AureoleLauncherTheme {
        OpenedWidgetPopup(
            config = OpenedWidgetPopupConfig(
                onDismiss = {},
                onResizeClick = {},
                onRemoveClick = {}
            )
        )
    }
}
