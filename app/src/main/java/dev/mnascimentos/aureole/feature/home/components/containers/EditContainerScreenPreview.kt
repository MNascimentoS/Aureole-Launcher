package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams

private const val PREVIEW_APP_COUNT = 3

private fun previewApps(labels: List<String>): List<AppInfo> = listOf(
    previewApp(index = 1, label = labels[0], color = 0xFF1B65C0.toInt()),
    previewApp(index = 2, label = labels[1], color = 0xFF2E7D32.toInt()),
    previewApp(index = 3, label = labels[2], color = 0xFFF57C00.toInt())
)

private fun previewApp(index: Int, label: String, color: Int): AppInfo = AppInfo(
    label = label,
    packageName = "com.example.app$index",
    componentName = ComponentName("com.example.app$index", "MainActivity"),
    icon = color.toDrawable()
)

private fun previewFolders(): List<AppFolder> = listOf(
    AppFolder(id = "folder_1", name = "Social", icon = "folder", appPackageNames = listOf("com.example.app1"))
)

private fun previewItems(panelId: String, apps: List<AppInfo>, folders: List<AppFolder>): List<ContainerItemEntity> =
    listOf(
        ContainerItemEntity(
            id = "item_f1",
            panelId = panelId,
            itemType = ContainerItemType.FOLDER,
            folderId = folders.first().id,
            orderIndex = 0
        )
    ) + apps.map { it.packageName }.mapIndexed { index, packageName ->
        ContainerItemEntity(
            id = "item_a${index + 1}",
            panelId = panelId,
            itemType = ContainerItemType.APP,
            packageName = packageName,
            orderIndex = index + 1
        )
    }

@Composable
private fun PreviewContainerScreen(panelId: String, title: String, orientation: String, appCount: Int) {
    val labels = (1..PREVIEW_APP_COUNT).map { stringResource(R.string.home_container_preview_app_label, it) }
    val apps = previewApps(labels = labels)
    val folders = previewFolders()
    val panel = ContainerModel(
        id = panelId,
        title = title,
        orientation = orientation,
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = folders,
        appPackageNames = apps.map { it.packageName },
        items = previewItems(panelId, apps.take(appCount), folders)
    )
    AureoleLauncherTheme {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = panel,
                allApps = apps,
                onDismiss = {},
                onSave = {}
            )
        )
    }
}

@AureolePreview
@Composable
private fun EditContainerScreenPreview() {
    PreviewContainerScreen(panelId = "panel_1", title = "Favoritos", orientation = "Vertical", appCount = 1)
}

@AureolePreview
@Composable
private fun EditContainerScreenHorizontalPreview() {
    PreviewContainerScreen(
        panelId = "panel_2",
        title = "Favoritos Horizontal",
        orientation = "Horizontal",
        appCount = 2
    )
}
