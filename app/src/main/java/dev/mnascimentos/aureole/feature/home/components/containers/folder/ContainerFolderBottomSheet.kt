package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItemParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

@Composable
internal fun ContainerFolderBottomSheet(
    params: ContainerFolderBottomSheetParams,
    onDismiss: () -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            ContainerFolderBottomSheetHeader()

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxSmall)
            ) {
                ContainerFolderMenuItems(params)
            }
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun ContainerFolderMenuItems(params: ContainerFolderBottomSheetParams) {
    AureolePopupMenuItem(
        title = stringResource(R.string.home_folder_edit_shortcuts),
        params = AureolePopupMenuItemParams(
            customIcon = {
                AureoleDS.icons.Star(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        ),
        onClick = params.onEditShortcuts
    )

    AureolePopupMenuItem(
        title = stringResource(R.string.home_folder_rename),
        params = AureolePopupMenuItemParams(
            customIcon = {
                AureoleDS.icons.Edit(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        ),
        onClick = params.onRename
    )

    AureolePopupMenuItem(
        title = stringResource(R.string.home_folder_icon_setting),
        params = AureolePopupMenuItemParams(
            customIcon = {
                AureoleDS.icons.Smile(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        ),
        onClick = params.onIcon
    )

    SecondaryFolderMenuItems(params)
}

@Composable
private fun SecondaryFolderMenuItems(params: ContainerFolderBottomSheetParams) {
    AureolePopupMenuItem(
        title = stringResource(R.string.home_folder_remove),
        params = AureolePopupMenuItemParams(
            customIcon = {
                AureoleDS.icons.Delete(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            isDestructive = false
        ),
        onClick = params.onRemoveFolder
    )

    AureolePopupMenuItem(
        title = stringResource(R.string.home_folder_container_settings),
        params = AureolePopupMenuItemParams(
            customIcon = {
                AureoleDS.icons.Settings(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        ),
        onClick = params.onOpenContainerSettings
    )
}

@Composable
private fun ContainerFolderBottomSheetHeader() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium)
    ) {
        AureoleText(
            text = stringResource(R.string.home_folder_settings),
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = AureoleDS.dimens.medium, bottom = AureoleDS.dimens.small),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )

        IconButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = AureoleDS.dimens.xxSmall)
                .size(AureoleDS.dimens.iconXLarge)
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun ContainerFolderBottomSheetPreview() {
    val sampleParams = ContainerFolderBottomSheetParams(
        onEditShortcuts = {},
        onRename = {},
        onIcon = {},
        onRemoveFolder = {},
        onOpenContainerSettings = {}
    )
    AureoleLauncherTheme {
        ContainerFolderBottomSheet(
            params = sampleParams,
            onDismiss = {}
        )
    }
}
