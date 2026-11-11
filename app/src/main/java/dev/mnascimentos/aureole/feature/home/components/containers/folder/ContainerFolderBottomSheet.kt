package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams

/**
 * Menu of a folder on the home (long press on the folder): Remove at the top, away from the thumb, and the
 * folder actions below it. "Edit shortcuts" is where apps are added to the folder; "Rename" and "Icon" open
 * the folder form.
 */
@Composable
internal fun ContainerFolderBottomSheet(
    params: ContainerFolderBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleMenuSheet(
        title = stringResource(R.string.home_folder_settings),
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> AureoleDS.icons.Folder(modifier = m, tint = AureoleDS.colors.onSurfaceMedium) },
            onSettingsClick = params.onOpenSettings
        )
    ) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_folder_remove),
                onClick = params.onRemoveFolder,
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true
            )
        )
        AureoleMenuSectionLabel(text = stringResource(R.string.home_folder_menu_section))
        ContainerFolderMenuItems(params)
    }
}

@Composable
private fun ContainerFolderMenuItems(params: ContainerFolderBottomSheetParams) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_container_settings),
            onClick = params.onOpenContainerSettings,
            icon = { m, c -> AureoleDS.icons.Settings(modifier = m, tint = c) },
            showChevron = true
        )
    )
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_icon_setting),
            onClick = params.onIcon,
            icon = { m, c -> AureoleDS.icons.Smile(modifier = m, tint = c) },
            showChevron = true
        )
    )
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_rename),
            onClick = params.onRename,
            icon = { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
            showChevron = true
        )
    )
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_edit_shortcuts),
            onClick = params.onEditShortcuts,
            icon = { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) },
            showChevron = true
        )
    )
}

@AureolePreview
@Composable
private fun ContainerFolderBottomSheetPreview() {
    val sampleParams = ContainerFolderBottomSheetParams(
        onEditShortcuts = {},
        onRename = {},
        onIcon = {},
        onRemoveFolder = {},
        onOpenContainerSettings = {},
        onOpenSettings = {}
    )
    AureoleLauncherTheme {
        ContainerFolderBottomSheet(
            params = sampleParams,
            onDismiss = {}
        )
    }
}
