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
import dev.mnascimentos.aureole.feature.home.model.ContainerAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.ContainerFolderBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.WidgetStackBottomSheetState

@PreviewTest
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "Opened Folder Grid Mode Flow", showBackground = true)
@Composable
fun HomeScreenOpenedFolderGridModeScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()
    val folder = sampleData.folders.first().copy(displayAsGrid = true)

    val customUiState = sampleData.uiState.copy(
        openedFolderId = folder.id,
        activeFolder = folder,
        activeFolderTopYPx = 150f
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "Opened Folder Popup Mode Flow", showBackground = true)
@Composable
fun HomeScreenOpenedFolderPopupModeScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()
    val folder = sampleData.folders.first().copy(displayAsGrid = false)

    val customUiState = sampleData.uiState.copy(
        openedFolderId = folder.id,
        activeFolder = folder,
        activeFolderTopYPx = 250f
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "Grid Edit Mode Flow", showBackground = true)
@Composable
fun HomeScreenGridEditModeScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    val customUiState = sampleData.uiState.copy(
        isGridEditMode = true
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "App Item Bottom Sheet Flow", showBackground = true)
@Composable
fun HomeScreenAppItemBottomSheetScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    val customUiState = sampleData.uiState.copy(
        activeContainerAppBottomSheet = ContainerAppBottomSheetState(
            app = sampleData.apps.first(),
            panelId = "panel_1"
        )
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "Container Folder Bottom Sheet Flow", showBackground = true)
@Composable
fun HomeScreenContainerFolderBottomSheetScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    val customUiState = sampleData.uiState.copy(
        activeContainerFolderBottomSheet = ContainerFolderBottomSheetState(
            folder = sampleData.folders.first(),
            panelId = "panel_1"
        )
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@Preview(name = "Widget Stack Bottom Sheet Flow", showBackground = true)
@Composable
fun HomeScreenWidgetStackBottomSheetScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    val customUiState = sampleData.uiState.copy(
        activeWidgetStackBottomSheet = WidgetStackBottomSheetState(
            widgetId = 101,
            stackId = "widget_stack_1"
        )
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

internal data class SampleHomeData(
    val apps: List<AppInfo>,
    val folders: List<AppFolder>,
    val uiState: MainUiState,
    val actions: HomeScreenActions
)

internal fun createSampleHomeData(): SampleHomeData {
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

    val uiState = MainUiState(
        apps = sampleApps,
        favoriteApps = favoriteApps,
        favoriteAppPackages = favoriteApps.map { it.packageName },
        folders = sampleFolders,
        gridItems = GridDefaults.getDefaultGridItems(),
        alphabet = listOf('B', 'C', 'M', 'P', 'S'),
        isLoading = false,
        isHazeEnabled = false,
        isContainerEnabled = true,
        showFolderLabels = true,
        showAllAppsOnHome = true
    )

    val actions = HomeScreenActions(
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

    return SampleHomeData(sampleApps, sampleFolders, uiState, actions)
}
