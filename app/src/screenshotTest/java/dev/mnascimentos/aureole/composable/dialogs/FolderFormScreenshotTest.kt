package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_EMOJIS
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyState
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormParams
import dev.mnascimentos.aureole.composable.dialogs.model.initialFolderDraft
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun FolderFormCase(
    isLeftHanded: Boolean,
    folder: AppFolder?,
    initialMode: FolderFormMode = FolderFormMode.FORM
) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            FolderFormSheet(
                params = FolderFormParams(
                    folder = folder,
                    onDelete = if (folder != null) ({}) else null,
                    initialMode = initialMode
                ),
                onDismiss = {},
                onSave = {}
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
            FolderFormGlyphCard(
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

/** Landscape: the folder on one side, the sheet on the other (pasta-form RF-05). */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun FolderFormNewLandscapeScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = null)

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun FolderFormEditLandscapeScreenshotTest() = FolderFormCase(isLeftHanded = false, folder = SampleFolder)

/** The color of the folder is a theme color: the same folder under another palette takes that palette's color. */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormEditOtherThemeScreenshotTest() {
    ProvideHomeUiState(MainUiState()) {
        AureoleLauncherTheme(
            AureoleThemeConfig(
                darkTheme = true,
                aureoleColors = getPaletteByName("Neon Dusk", isDarkTheme = true).colors
            )
        ) {
            FolderFormDialog(folder = SampleFolder, onDismiss = {}, onSave = {}, onDelete = {})
        }
    }
}

/**
 * "All icons" inside the sheet (pasta-form RF-01/RF-02/RF-05, RF-15): the shared AureoleIconCatalogGrid with the
 * chips fixed on top, the category name stuck over the grid and the saved icon filled.
 */
@PreviewTest
@AureolePreview
@Composable
private fun FolderAllIconsRightHandedScreenshotTest() =
    FolderFormCase(isLeftHanded = false, folder = SampleFolder, initialMode = FolderFormMode.ALL_ICONS)

@PreviewTest
@AureolePreview
@Composable
private fun FolderAllIconsLeftHandedScreenshotTest() =
    FolderFormCase(isLeftHanded = true, folder = SampleFolder, initialMode = FolderFormMode.ALL_ICONS)

/** The end of the edit form, which the sheet only shows after scrolling: "Delete folder" last, after a divider (RF-06). */
@PreviewTest
@AureolePreview
@Composable
private fun FolderFormDeleteAtEndScreenshotTest() {
    ProvideHomeUiState(MainUiState()) {
        AureoleLauncherTheme {
            Column(
                modifier = Modifier
                    .background(AureoleDS.colors.surface)
                    .padding(AureoleDS.dimens.small)
            ) {
                FolderFormBody(
                    state = FolderFormBodyState(draft = initialFolderDraft(SampleFolder), showDelete = true),
                    actions = FolderFormBodyActions(onDraftChange = {}, onOpenAllIcons = {}, onDelete = {})
                )
            }
        }
    }
}
