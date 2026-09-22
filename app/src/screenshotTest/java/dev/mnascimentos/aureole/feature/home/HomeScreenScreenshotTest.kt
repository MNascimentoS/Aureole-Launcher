package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@PreviewTest
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenScreenshotTest() {
    val context = LocalContext.current

    val cameraApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val phoneApp = AppInfo(
        label = "Phone",
        packageName = "com.example.phone",
        componentName = ComponentName("com.example.phone", "MainActivity"),
        icon = ColorDrawable(0xFF2E7D32.toInt())
    )
    val messagesApp = AppInfo(
        label = "Messages",
        packageName = "com.example.messages",
        componentName = ComponentName("com.example.messages", "MainActivity"),
        icon = ColorDrawable(0xFFF57C00.toInt())
    )
    val browserApp = AppInfo(
        label = "Browser",
        packageName = "com.example.browser",
        componentName = ComponentName("com.example.browser", "MainActivity"),
        icon = ColorDrawable(0xFF0288D1.toInt())
    )
    val settingsApp = AppInfo(
        label = "Settings",
        packageName = "com.example.settings",
        componentName = ComponentName("com.example.settings", "MainActivity"),
        icon = ColorDrawable(0xFF5D4037.toInt())
    )

    val sampleApps = listOf(browserApp, cameraApp, messagesApp, phoneApp, settingsApp)
    val favoriteApps = listOf(phoneApp, messagesApp, cameraApp, browserApp)

    val sampleFolders = listOf(
        AppFolder(id = "folder_work", name = "Work", icon = "briefcase"),
        AppFolder(id = "folder_social", name = "Social", icon = "message")
    )

    val firstInstallationUiState = MainUiState(
        apps = sampleApps,
        favoriteApps = favoriteApps,
        favoriteAppPackages = favoriteApps.map { it.packageName },
        folders = sampleFolders,
        gridItems = GridDefaults.getDefaultGridItems(),
        alphabet = listOf('B', 'C', 'M', 'P', 'S'),
        isLoading = false,
        isHazeEnabled = false,
        isSidePanelEnabled = true,
        showFolderLabels = true,
        showAllAppsOnHome = true
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
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides firstInstallationUiState,
            LocalHomeActions provides mockActions
        ) {
            HomeScreen(
                appWidgetHost = AppWidgetHost(context, 1024)
            )
        }
    }
}
