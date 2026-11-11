package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.dialogs.model.FolderIconPickerActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderIconPickerState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.icons.FolderIconCategory
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
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

@Composable
private fun FolderIconGridCase(isLeftHanded: Boolean, category: FolderIconCategory, selectedIcon: String) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            FolderIconGrid(
                state = FolderIconPickerState(selectedIcon, category),
                actions = FolderIconPickerActions(onIconSelected = {}, onCategorySelected = {}),
                modifier = Modifier.padding(AureoleDS.dimens.medium),
                isLeftHanded = isLeftHanded
            )
        }
    }
}

/** Icon picker: category chips on top, 5-column grid, the saved icon outlined. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderIconGridRightHandedScreenshotTest() =
    FolderIconGridCase(isLeftHanded = false, category = FolderIconCategory.MEDIA, selectedIcon = "music_note")

@PreviewTest
@AureolePreview
@Composable
private fun FolderIconGridLeftHandedScreenshotTest() =
    FolderIconGridCase(isLeftHanded = true, category = FolderIconCategory.MEDIA, selectedIcon = "music_note")
