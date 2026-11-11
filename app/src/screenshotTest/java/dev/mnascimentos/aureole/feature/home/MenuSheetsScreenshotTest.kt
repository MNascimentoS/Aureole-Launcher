package dev.mnascimentos.aureole.feature.home

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPopup
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

private val SAMPLE_APPS = listOf(
    "Phone" to 0xFF2E7D32,
    "Messages" to 0xFFF57C00,
    "Camera" to 0xFF1B65C0,
    "Browser" to 0xFF1D8FE0
).map { (label, color) ->
    AppInfo(
        label = label,
        packageName = "com.example.${label.lowercase()}",
        componentName = ComponentName("com.example.${label.lowercase()}", "MainActivity"),
        icon = color.toInt().toDrawable()
    )
}

@Composable
private fun MenuCase(isLeftHanded: Boolean, content: @Composable () -> Unit) {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState.copy(isLeftHandedMode = isLeftHanded),
            LocalHomeActions provides sampleData.actions
        ) {
            content()
        }
    }
}

@Composable
private fun AppMenuCase(isLeftHanded: Boolean) = MenuCase(isLeftHanded) {
    AppItemBottomSheet(
        params = AppItemBottomSheetParams(
            app = SAMPLE_APPS.last(),
            isFavorite = true,
            actions = AppItemRowActions(onToggleFavorite = {}, onOpenSettings = {}),
            onOpenContainerSettings = {}
        ),
        onDismiss = {}
    )
}

@Composable
private fun FolderMenuCase(isLeftHanded: Boolean) = MenuCase(isLeftHanded) {
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

@Composable
private fun ItemMenuCase(isLeftHanded: Boolean) = MenuCase(isLeftHanded) {
    EditGridItemDialog(
        item = LauncherItemState(
            id = "clock",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        ),
        onDismissRequest = {},
        onDeleteConfirm = {}
    )
}

@Composable
private fun FolderPopupCase(isLeftHanded: Boolean, isGrid: Boolean) = MenuCase(isLeftHanded) {
    OpenedFolderPopup(
        config = OpenedFolderPopupConfig(
            folder = AppFolder(id = "f1", name = "Work", appPackageNames = SAMPLE_APPS.map { it.packageName }),
            allApps = SAMPLE_APPS,
            actions = FolderPopupActions(onDismiss = {}, onAppClick = {}),
            isGridFolderEnabled = isGrid
        )
    )
}

@PreviewTest
@AureolePreview
@Composable
private fun AppMenuRightHandedScreenshotTest() = AppMenuCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun AppMenuLeftHandedScreenshotTest() = AppMenuCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun FolderMenuRightHandedScreenshotTest() = FolderMenuCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FolderMenuLeftHandedScreenshotTest() = FolderMenuCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun ItemMenuRightHandedScreenshotTest() = ItemMenuCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ItemMenuLeftHandedScreenshotTest() = ItemMenuCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPopupGridRightHandedScreenshotTest() = FolderPopupCase(isLeftHanded = false, isGrid = true)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPopupGridLeftHandedScreenshotTest() = FolderPopupCase(isLeftHanded = true, isGrid = true)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPopupListRightHandedScreenshotTest() = FolderPopupCase(isLeftHanded = false, isGrid = false)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPopupListLeftHandedScreenshotTest() = FolderPopupCase(isLeftHanded = true, isGrid = false)
