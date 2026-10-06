package dev.mnascimentos.aureole.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.feature.home.components.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.home.folder.EditFolderDialog
import dev.mnascimentos.aureole.feature.home.folder.FolderAppPickerDialog
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog

@PreviewTest
@Preview(name = "Favorite Apps Dialog Flow", showBackground = true)
@Composable
fun FavoriteAppsDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    val config = FavoriteAppsDialogConfig(
        allApps = sampleData.apps,
        favoriteAppPackages = listOf("com.example.phone", "com.example.camera")
    )
    val actions = FavoriteAppsDialogActions(
        onDismiss = {}
    )
    AureoleLauncherTheme {
        FavoriteAppsDialog(config = config, actions = actions)
    }
}

@PreviewTest
@Preview(name = "Edit Container Dialog Flow", showBackground = true)
@Composable
fun EditContainerDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    val item = LauncherItemState(
        id = "item_1",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState,
            LocalHomeActions provides sampleData.actions
        ) {
            EditGridItemDialog(
                item = item,
                onDismissRequest = {},
                onDeleteConfirm = {}
            )
        }
    }
}

@PreviewTest
@Preview(name = "Add Container Dialog Flow", showBackground = true)
@Composable
fun AddContainerDialogScreenshotTest() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {}
        )
    }
}

@PreviewTest
@Preview(name = "Create Folder Dialog Flow", showBackground = true)
@Composable
fun CreateFolderDialogScreenshotTest() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = {}
        )
    }
}

@PreviewTest
@Preview(name = "Edit Folder Dialog Flow", showBackground = true)
@Composable
fun EditFolderDialogScreenshotTest() {
    val folder = AppFolder(id = "folder_work", name = "Work", icon = "briefcase")
    AureoleLauncherTheme {
        EditFolderDialog(
            folder = folder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}

@PreviewTest
@Preview(name = "Folder App Picker Dialog Flow", showBackground = true)
@Composable
fun FolderAppPickerDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = sampleData.folders.first(),
            allApps = sampleData.apps,
            onDismiss = {},
            onSave = {}
        )
    }
}

@PreviewTest
@Preview(name = "Update Available Dialog Flow", showBackground = true)
@Composable
fun UpdateAvailableDialogScreenshotTest() {
    AureoleLauncherTheme {
        UpdateAvailableDialog(
            onConfirmUpdate = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@Preview(name = "Update Downloaded Dialog Flow", showBackground = true)
@Composable
fun UpdateDownloadedDialogScreenshotTest() {
    AureoleLauncherTheme {
        UpdateDownloadedDialog(
            onConfirmRestart = {},
            onDismiss = {}
        )
    }
}
