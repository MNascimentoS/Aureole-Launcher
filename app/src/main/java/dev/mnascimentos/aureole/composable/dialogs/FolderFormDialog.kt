package dev.mnascimentos.aureole.composable.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyState
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.composable.dialogs.model.effectiveIcon
import dev.mnascimentos.aureole.composable.dialogs.model.initialFolderDraft
import dev.mnascimentos.aureole.composable.dialogs.model.resolveFolderName
import dev.mnascimentos.aureole.composable.dialogs.model.toDetails
import dev.mnascimentos.aureole.composable.sheets.AureoleAutoSaveOverlay
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * New / edit folder sheet (pasta-form): the folder is anchored above the sheet with its name pill under it (the
 * name is typed there; a new folder opens with the keyboard), and the sheet holds, in one scroll, what the folder
 * shows (Icon | Letter | Emoji), its color and, when editing, "Delete folder" at the end. "All icons" swaps the
 * sheet content for the grid of every icon; back returns to the form. In landscape the overlay puts the folder on
 * one side and the sheet on the other. No logo in the title.
 *
 * Editing has no buttons: closing the sheet saves what changed. Creating has a single button, "Create and pick
 * apps" when [pickAppsOnCreate] (the caller then opens the app picker of the new folder) or "Create".
 */
@Composable
fun FolderFormDialog(
    onDismiss: () -> Unit,
    onSave: (FolderDetails) -> Unit,
    modifier: Modifier = Modifier,
    folder: AppFolder? = null,
    onDelete: (() -> Unit)? = null,
    pickAppsOnCreate: Boolean = true
) {
    val isLeftHanded = LocalLeftHanded.current
    val isEditing = folder != null
    val placeholder = stringResource(R.string.home_folder_name_placeholder)
    val initial = remember { initialFolderDraft(folder) }
    var draft by remember { mutableStateOf(initial) }
    var mode by remember { mutableStateOf(FolderFormMode.FORM) }
    val save = { onSave(draft.toDetails(placeholder)) }

    val title = stringResource(
        when {
            mode == FolderFormMode.ALL_ICONS -> R.string.home_folder_all_icons
            isEditing -> R.string.home_folder_edit_title
            else -> R.string.home_folder_new_title
        }
    )
    val preview: @Composable BoxScope.() -> Unit = {
        FolderFormPreview(
            draft = draft,
            label = resolveFolderName(draft.name, placeholder),
            onNameChange = { draft = draft.copy(name = it) },
            modifier = Modifier.align(Alignment.Center),
            autoFocus = !isEditing
        )
    }
    val body: @Composable ColumnScope.() -> Unit = {
        if (mode == FolderFormMode.ALL_ICONS) {
            // Inside the sheet, so it is registered after the overlay's own back handler and wins over it.
            BackHandler { mode = FolderFormMode.FORM }
            FolderAllIconsBody(
                selectedKey = draft.effectiveIcon(),
                onSelect = {
                    draft = draft.copy(icon = it, iconType = FolderIconType.ICON)
                    mode = FolderFormMode.FORM
                },
                onBack = { mode = FolderFormMode.FORM }
            )
        } else {
            FolderFormBody(
                state = FolderFormBodyState(draft = draft, showDelete = isEditing && onDelete != null),
                actions = FolderFormBodyActions(
                    onDraftChange = { draft = it },
                    onOpenAllIcons = { mode = FolderFormMode.ALL_ICONS },
                    onDelete = onDelete
                )
            )
        }
    }

    if (isEditing) {
        AureoleAutoSaveOverlay(
            params = AutoSaveOverlayParams(
                title = title,
                isChanged = { draft.toDetails(placeholder) != initial.toDetails(placeholder) },
                onCommit = save,
                onDismissRequest = onDismiss,
                isLeftHanded = isLeftHanded
            ),
            modifier = modifier,
            preview = preview,
            content = body
        )
    } else {
        AureoleConfirmOverlay(
            params = ConfirmOverlayParams(
                title = title,
                primaryLabel = stringResource(
                    if (pickAppsOnCreate) R.string.home_folder_create_pick_apps else R.string.core_common_create
                ),
                onPrimary = save,
                onDismissRequest = onDismiss,
                isLeftHanded = isLeftHanded,
                hasSecondary = false
            ),
            modifier = modifier,
            preview = preview,
            content = body
        )
    }
}

@AureolePreview
@Composable
private fun FolderFormDialogNewPreview() {
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = null,
            onDismiss = {},
            onSave = {}
        )
    }
}

@AureolePreview
@Composable
private fun FolderFormDialogEditPreview() {
    val mockFolder = AppFolder(id = "1", name = "Tools", icon = "build")
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = mockFolder,
            onDismiss = {},
            onSave = {},
            onDelete = {}
        )
    }
}
