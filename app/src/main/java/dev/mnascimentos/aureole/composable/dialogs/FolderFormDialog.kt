package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormSheetActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormSheetState
import dev.mnascimentos.aureole.composable.dialogs.model.resolveFolderName
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

/**
 * New / edit folder form in an edit overlay: the folder (icon and name) is anchored above the sheet and follows
 * what is typed, the icon grid scrolls inside it, and the name field sits glued to the Cancel / Create (Save)
 * row, which rides above the keyboard. The filled button is on the thumb side.
 */
@Composable
fun FolderFormDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String?) -> Unit,
    modifier: Modifier = Modifier,
    folder: AppFolder? = null,
    onDelete: (() -> Unit)? = null
) {
    val isLeftHanded = LocalHomeUiState.current.isLeftHandedMode
    val isEditing = folder != null
    val placeholderName = stringResource(R.string.home_folder_name_placeholder)
    var folderName by remember { mutableStateOf(folder?.name.orEmpty()) }
    var selectedIcon by remember { mutableStateOf(folder?.icon) }

    AureoleConfirmOverlay(
        params = ConfirmOverlayParams(
            title = stringResource(if (isEditing) R.string.home_folder_edit_title else R.string.home_folder_new_title),
            primaryLabel = stringResource(if (isEditing) R.string.core_common_save else R.string.core_common_create),
            onPrimary = { onSave(resolveFolderName(folderName, placeholderName), selectedIcon) },
            onDismissRequest = onDismiss,
            isLeftHanded = isLeftHanded
        ),
        modifier = modifier,
        preview = {
            AureolePreviewFrame(
                label = stringResource(R.string.home_folder_form_preview),
                modifier = Modifier.align(Alignment.Center)
            ) {
                FolderIconPreview(
                    folderName = folderName,
                    selectedIcon = selectedIcon,
                    label = resolveFolderName(folderName, placeholderName),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        bottomExtra = {
            FolderNameField(
                value = folderName,
                onValueChange = { folderName = it },
                modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
            )
        }
    ) {
        FolderFormSheetBody(
            state = FolderFormSheetState(
                resolveFolderName(folderName, placeholderName),
                selectedIcon,
                showDelete = isEditing && onDelete != null
            ),
            actions = FolderFormSheetActions(onIconSelected = { selectedIcon = it }, onDelete = onDelete),
            isLeftHanded = isLeftHanded
        )
    }
}

@Composable
private fun FolderFormSheetBody(
    state: FolderFormSheetState,
    actions: FolderFormSheetActions,
    isLeftHanded: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        actions.onDelete?.takeIf { state.showDelete }?.let { onDelete ->
            AureoleMenuRow(
                MenuRowParams(
                    title = stringResource(R.string.home_folder_form_delete),
                    onClick = onDelete,
                    icon = { m, tint -> AureoleDS.icons.Delete(m, tint = tint) },
                    isDestructive = true
                )
            )
        }
        AureoleText(
            text = stringResource(R.string.home_folder_icon).uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )
        FolderIconGrid(
            selectedIcon = state.selectedIcon,
            onIconSelected = actions.onIconSelected,
            folderName = state.folderName,
            isLeftHanded = isLeftHanded
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
            onSave = { _, _ -> }
        )
    }
}

@AureolePreview
@Composable
private fun FolderFormDialogEditPreview() {
    val mockFolder = AppFolder(id = "1", name = "Tools")
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = mockFolder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}
