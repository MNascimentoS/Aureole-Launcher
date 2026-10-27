package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerColumnConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRowConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
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
