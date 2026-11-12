package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuListSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams

/**
 * Menu of a folder on the home (long press on the folder): the folder actions first and Remove last, near the
 * thumb and in red. "Edit shortcuts" is where apps are added to the folder; "Rename" and "Icon" open the folder form.
 */
@Composable
internal fun ContainerFolderBottomSheet(
    params: ContainerFolderBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = folderMenuEntries(params)
    AureoleMenuListSheet(
        title = stringResource(R.string.home_folder_settings),
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> AureoleDS.icons.Folder(modifier = m, tint = AureoleDS.colors.onSurfaceMedium) },
            onSettingsClick = params.onOpenSettings
        )
    ) { entries }
}

@Composable
private fun folderMenuEntries(params: ContainerFolderBottomSheetParams): List<MenuEntry> = listOf(
    MenuEntry.Section(text = stringResource(R.string.home_folder_menu_section)),
    folderRow(R.string.home_folder_edit_shortcuts, params.onEditShortcuts) { m, c ->
        AureoleDS.icons.AllApps(modifier = m, tint = c)
    },
    folderRow(R.string.home_folder_rename, params.onRename) { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
    folderRow(R.string.home_folder_icon_setting, params.onIcon) { m, c -> AureoleDS.icons.Smile(modifier = m, tint = c) },
    folderRow(R.string.home_folder_container_settings, params.onOpenContainerSettings) { m, c ->
        AureoleDS.icons.VerticalContainer(modifier = m, tint = c)
    },
    MenuEntry.Row(
        MenuRowParams(
            title = stringResource(R.string.home_folder_remove),
            onClick = params.onRemoveFolder,
            icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
            isDestructive = true
        )
    )
)

@Composable
private fun folderRow(
    @StringRes titleRes: Int,
    onClick: () -> Unit,
    icon: @Composable (Modifier, Color) -> Unit
): MenuEntry = MenuEntry.Row(
    MenuRowParams(title = stringResource(titleRes), onClick = onClick, icon = icon, showChevron = true)
)

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
