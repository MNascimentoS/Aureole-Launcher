package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.dialogs.FolderFormDialog
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private fun createMockActions() = HomeScreenActions(
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

@PreviewTest
@AureolePreview
@Composable
private fun WhenFolderMiniGridRendered_shouldRenderMiniGridScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val folder = AppFolder(
        id = "f1",
        name = "Social",
        appPackageNames = listOf("com.example.camera")
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
            LocalLeftHanded provides (MainUiState(apps = listOf(sampleApp))).isLeftHandedMode,
            LocalHomeActions provides createMockActions()
        ) {
            Column {
                FolderMiniGridPreview(
                    folder = folder,
                    allApps = listOf(sampleApp)
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenContainerFolderItemRendered_shouldRenderItemScreenshotTest() {
    val folder = AppFolder(
        id = "f1",
        name = "Media",
        icon = "star"
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalLeftHanded provides (MainUiState()).isLeftHandedMode,
            LocalHomeActions provides createMockActions()
        ) {
            ContainerFolderItem(
                params = ContainerFolderItemParams(
                    folder = folder,
                    isOpened = false,
                    config = ContainerFolderConfig(
                        showFolderLabels = true,
                        isGridFolderEnabled = false,
                        isBackgroundEnabled = true
                    ),
                    onFolderClick = { _, _ -> }
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenContainerFolderBottomSheetRendered_shouldRenderBottomSheetScreenshotTest() {
    AureoleLauncherTheme {
        ContainerFolderBottomSheet(
            params = ContainerFolderBottomSheetParams(
                onEditShortcuts = {},
                onRename = {},
                onIcon = {},
                onRemoveFolder = {},
                onOpenContainerSettings = {},
                onOpenSettings = {}
            ),
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenFolderFormDialogRendered_shouldRenderFormDialogScreenshotTest() {
    val folder = AppFolder(id = "f1", name = "Tools", icon = "build")
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = folder,
            onDismiss = {},
            onSave = {},
            onDelete = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenFolderDialogRendered_shouldRenderFolderDialogScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val folder = AppFolder(
        id = "f1",
        name = "Photos",
        appPackageNames = listOf("com.example.camera")
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalLeftHanded provides (MainUiState()).isLeftHandedMode,
            LocalHomeActions provides createMockActions()
        ) {
            FolderDialog(
                folder = folder,
                allApps = listOf(sampleApp),
                onDismiss = {},
                onAppClick = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenFolderAppPickerDialogRendered_shouldRenderPickerScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val folder = AppFolder(
        id = "f1",
        name = "Apps",
        appPackageNames = listOf("com.example.camera")
    )

    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = folder,
            allApps = listOf(sampleApp),
            onDismiss = {},
            onSave = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WhenOpenedFolderPopupRendered_shouldRenderPopupScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val folder = AppFolder(
        id = "f1",
        name = "Opened Folder",
        appPackageNames = listOf("com.example.camera")
    )
    val actions = FolderPopupActions(
        onDismiss = {},
        onAppClick = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalLeftHanded provides (MainUiState()).isLeftHandedMode,
            LocalHomeActions provides createMockActions()
        ) {
            OpenedFolderPopup(
                config = OpenedFolderPopupConfig(
                    folder = folder,
                    allApps = listOf(sampleApp),
                    actions = actions
                )
            )
        }
    }
}

/**
 * RF-17: a folder opened from the last item of the container, at the bottom of the screen, moves up until its bottom
 * is 16 dp above the navigation bar instead of going under it (previews have no bars: 16 dp above the bottom).
 */
@PreviewTest
@AureolePreview
@Composable
private fun WhenFolderOpensFromTheLastItem_shouldStayAboveTheNavigationBarScreenshotTest() {
    val apps = (1..6).map { index ->
        AppInfo(
            label = "App $index",
            packageName = "com.example.app$index",
            componentName = ComponentName("com.example.app$index", "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        )
    }
    val folder = AppFolder(id = "f1", name = "Work", appPackageNames = apps.map { it.packageName })
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalLeftHanded provides false,
            LocalHomeActions provides createMockActions()
        ) {
            Box(modifier = Modifier.height(PHONE_HEIGHT).fillMaxWidth()) {
                OpenedFolderPlacementLayout(
                    // The folder's top is almost at the bottom of the screen.
                    anchorTopPx = with(LocalDensity.current) { (PHONE_HEIGHT - 80.dp).toPx() },
                    isGridFolder = false,
                    isLeftHanded = false
                ) {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = folder,
                            allApps = apps,
                            actions = FolderPopupActions(onDismiss = {}, onAppClick = {})
                        ),
                        modifier = Modifier.padding(end = 88.dp)
                    )
                }
            }
        }
    }
}

private val PHONE_HEIGHT = 640.dp

