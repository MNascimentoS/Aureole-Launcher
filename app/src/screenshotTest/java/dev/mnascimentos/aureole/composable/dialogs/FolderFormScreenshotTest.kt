package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun FolderFormCase(isLeftHanded: Boolean, folder: AppFolder?) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            FolderFormDialog(
                folder = folder,
                onDismiss = {},
                onSave = { _, _ -> },
                onDelete = if (folder != null) ({}) else null
            )
        }
    }
}

private val SampleFolder = AppFolder(id = "1", name = "Work", icon = "star")

/** New folder: preview with the placeholder name, icon grid, name field glued to Cancel / Create. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormNewRightHandedScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = null)

@PreviewTest
@AureolePreview
@Composable
private fun FolderFormNewLeftHandedScreenshotTest() = FolderFormCase(isLeftHanded = true, folder = null)

/** Edit folder: the "Delete folder" row on top, the current name and icon, Cancel / Save. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEditRightHandedScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = SampleFolder)

@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEditLeftHandedScreenshotTest() = FolderFormCase(isLeftHanded = true, folder = SampleFolder)
