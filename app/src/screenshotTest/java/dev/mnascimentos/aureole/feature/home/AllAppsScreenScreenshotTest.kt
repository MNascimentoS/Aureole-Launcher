package dev.mnascimentos.aureole.feature.home

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal fun createMockHomeScreenActions(): HomeScreenActions = HomeScreenActions(
    onWidgetRowHeightChanged = {},
    onAddWidgetClick = {},
    onRemoveWidgetClick = { _, _ -> },
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

@PreviewTest
@AureolePreview
@Composable
private fun AllAppsScreenScreenshotTest() {
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

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides createMockHomeScreenActions()
        ) {
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AllAppsScreenWithSearchQueryScreenshotTest() {
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

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides createMockHomeScreenActions()
        ) {
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AllAppsScreenWithEmptySearchQueryScreenshotTest() {

    val mockUiState = MainUiState(
        apps = emptyList(),
        filteredApps = emptyList(),
        searchQuery = "XYZNonExistent",
        alphabet = emptyList(),
        isLoading = false,
        isHazeEnabled = false
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides createMockHomeScreenActions()
        ) {
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}
