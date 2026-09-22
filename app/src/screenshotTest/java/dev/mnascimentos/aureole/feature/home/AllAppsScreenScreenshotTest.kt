package dev.mnascimentos.aureole.feature.home

import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@PreviewTest
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AllAppsScreenScreenshotTest() {
    val sampleApps = listOf(
        AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        ),
        AppInfo(
            label = "Gallery",
            packageName = "com.example.gallery",
            componentName = ComponentName("com.example.gallery", "MainActivity"),
            icon = ColorDrawable(0xFF43A047.toInt())
        ),
        AppInfo(
            label = "Settings",
            packageName = "com.example.settings",
            componentName = ComponentName("com.example.settings", "MainActivity"),
            icon = ColorDrawable(0xFFE53935.toInt())
        )
    )

    val mockUiState = MainUiState(
        apps = sampleApps,
        filteredApps = sampleApps,
        alphabet = listOf('C', 'G', 'S'),
        isLoading = false,
        isHazeEnabled = false
    )

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
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}

@PreviewTest
@Preview(name = "Search Filtered", showBackground = true)
@Composable
fun AllAppsScreenWithSearchQueryScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    val mockUiState = MainUiState(
        apps = listOf(sampleApp),
        filteredApps = listOf(sampleApp),
        searchQuery = "Cam",
        alphabet = listOf('C'),
        isLoading = false,
        isHazeEnabled = false
    )

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
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}
