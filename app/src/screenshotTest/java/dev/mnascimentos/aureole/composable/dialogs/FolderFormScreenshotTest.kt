package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_EMOJIS
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun FolderFormCase(isLeftHanded: Boolean, folder: AppFolder?) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            FolderFormDialog(
                folder = folder,
                onDismiss = {},
                onSave = {},
                onDelete = if (folder != null) ({}) else null
            )
        }
    }
}

private val SampleFolder = AppFolder(
    id = "1",
    name = "Bank",
    icon = "account_balance",
    color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
)

private val EmojiFolder = SampleFolder.copy(iconType = FolderIconType.EMOJI, emoji = FOLDER_EMOJIS.first())

/** New folder: empty name pill under the tile, Icon segment with the suggestions, "Create and pick apps". */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormNewRightHandedScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = null)

@PreviewTest
@AureolePreview
@Composable
private fun FolderFormNewLeftHandedScreenshotTest() = FolderFormCase(isLeftHanded = true, folder = null)

/** Edit folder: own color, the saved icon marked, "Delete folder" at the end, no buttons. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEditRightHandedScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = SampleFolder)

@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEditLeftHandedScreenshotTest() = FolderFormCase(isLeftHanded = true, folder = SampleFolder)

/** Edit an emoji folder: Emoji segment with the common emojis and the "+" tile. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEmojiScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = EmojiFolder)

/** The preview over the sheet: the tile with its own color and the name pill. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormPreviewTileScreenshotTest() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            FolderFormPreview(
                draft = FolderFormDraft(
                    name = "Bank",
                    color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
                ),
                label = "Bank",
                onNameChange = {}
            )
        }
    }
}
