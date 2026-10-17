package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Suppress("LongMethod") // Compose preview rendering sample container horizontal layout
@AureolePreview
@Composable
private fun EditContainerScreenPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "App 1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        ),
        AppInfo(
            label = "App 2",
            packageName = "com.example.app2",
            componentName = ComponentName("com.example.app2", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        ),
        AppInfo(
            label = "App 3",
            packageName = "com.example.app3",
            componentName = ComponentName("com.example.app3", "MainActivity"),
            icon = 0xFFF57C00.toInt().toDrawable()
        )
    )
    val sampleFolders = listOf(
        AppFolder(id = "folder_1", name = "Social", icon = "folder", appPackageNames = listOf("com.example.app1"))
    )
    val panel = ContainerModel(
        id = "panel_1",
        title = "Favoritos",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleFolders,
        appPackageNames = sampleApps.map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_1",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleFolders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_1",
                itemType = ContainerItemType.APP,
                packageName = sampleApps.first().packageName,
                orderIndex = 1
            )
        )
    )
    AureoleLauncherTheme {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = panel,
                allApps = sampleApps,
                onDismiss = {},
                onSave = {}
            )
        )
    }
}

@Suppress("LongMethod") // Compose preview rendering sample container horizontal layout
@AureolePreview
@Composable
private fun EditContainerScreenHorizontalPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "App 1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        ),
        AppInfo(
            label = "App 2",
            packageName = "com.example.app2",
            componentName = ComponentName("com.example.app2", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        ),
        AppInfo(
            label = "App 3",
            packageName = "com.example.app3",
            componentName = ComponentName("com.example.app3", "MainActivity"),
            icon = 0xFFF57C00.toInt().toDrawable()
        )
    )
    val sampleFolders = listOf(
        AppFolder(id = "folder_1", name = "Social", icon = "folder", appPackageNames = listOf("com.example.app1"))
    )
    val panel = ContainerModel(
        id = "panel_2",
        title = "Favoritos Horizontal",
        orientation = "Horizontal",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleFolders,
        appPackageNames = sampleApps.map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_2",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleFolders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_2",
                itemType = ContainerItemType.APP,
                packageName = sampleApps.first().packageName,
                orderIndex = 1
            ),
            ContainerItemEntity(
                id = "item_a2",
                panelId = "panel_2",
                itemType = ContainerItemType.APP,
                packageName = sampleApps[1].packageName,
                orderIndex = 2
            )
        )
    )
    AureoleLauncherTheme {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = panel,
                allApps = sampleApps,
                onDismiss = {},
                onSave = {}
            )
        )
    }
}
