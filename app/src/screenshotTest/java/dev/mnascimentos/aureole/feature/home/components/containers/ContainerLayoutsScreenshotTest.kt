package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerColumnConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRowConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.containerConfigFor
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onboardingContainerModel
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private fun mockHomeScreenActions() = HomeScreenActions(
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
private fun ContainerLayoutsScreenshotTest() {
    val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
            LocalLeftHanded provides (MainUiState(apps = listOf(sampleApp))).isLeftHandedMode,
            LocalHomeActions provides mockHomeScreenActions()
        ) {
            Column {
                ContainerColumn(
                    config = ContainerConfig(
                        panelId = "preview_column",
                        appPackageNames = listOf("com.example.camera"),
                        showFolderLabels = true,
                        isBackgroundEnabled = true
                    ),
                    columnConfig = ContainerColumnConfig(
                        backgroundColor = AureoleDS.colors.surface,
                        verticalArrangement = Arrangement.Center,
                        onFolderClick = { _, _ -> }
                    )
                )

                ContainerRow(
                    config = ContainerConfig(
                        panelId = "preview_row",
                        appPackageNames = listOf("com.example.camera"),
                        showFolderLabels = true,
                        isBackgroundEnabled = true
                    ),
                    rowConfig = ContainerRowConfig(
                        backgroundColor = AureoleDS.colors.surface,
                        horizontalArrangement = Arrangement.Center,
                        onFolderClick = { _, _ -> }
                    )
                )
            }
        }
    }
}

/**
 * The container after the onboarding places four folders in it (ajustes-onboarding RF-11/RF-12): the config comes
 * from [onboardingContainerModel], the one `placeSilently` saves, so it shows no background, no folder labels and
 * the folders as a grid of their apps.
 */
@PreviewTest
@AureolePreview
@Composable
private fun ContainerFourFoldersScreenshotTest() {
    val appColors = listOf(0xFF1B65C0, 0xFF2E7D32, 0xFFC62828, 0xFFF9A825, 0xFF6A1B9A, 0xFF00838F)
    val apps = appColors.mapIndexed { index, color ->
        AppInfo(
            label = "App $index",
            packageName = "com.example.app$index",
            componentName = ComponentName("com.example.app$index", "MainActivity"),
            icon = ColorDrawable(color.toInt())
        )
    }
    val folders = listOf("Bank", "Work", "Games", "Travel").mapIndexed { index, name ->
        AppFolder(
            id = "f$index",
            name = name,
            appPackageNames = apps.drop(index).take(FOLDER_APP_COUNT).map { it.packageName }
        )
    }
    val item = LauncherItemState(id = "onboarding_container", type = LauncherItemType.SHORTCUTS_CONTAINER)
    val model = onboardingContainerModel(id = item.id, position = "Center", isExpandCell = false)
    val uiState = MainUiState(apps = apps, containers = mapOf(item.id to model.copy(folders = folders)))
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides false,
            LocalHomeActions provides mockHomeScreenActions()
        ) {
            ContainerColumn(
                config = uiState.containerConfigFor(item, hazeState = null),
                columnConfig = ContainerColumnConfig(
                    backgroundColor = AureoleDS.colors.surface,
                    verticalArrangement = Arrangement.Center,
                    onFolderClick = { _, _ -> }
                )
            )
        }
    }
}

private const val FOLDER_APP_COUNT = 3
