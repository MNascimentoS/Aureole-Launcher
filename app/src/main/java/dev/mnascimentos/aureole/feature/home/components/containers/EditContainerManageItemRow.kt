package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.dialogs.FolderBadge
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.ChevronRight
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.DragHandle
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.displayTitle

private fun resolveFolder(item: ContainerItemEntity, folders: List<AppFolder>): AppFolder? {
    if (item.itemType != ContainerItemType.FOLDER) return null
    return folders.find { it.id == item.folderId }
}

private fun resolveApp(item: ContainerItemEntity, allApps: List<AppInfo>): AppInfo? {
    if (item.itemType != ContainerItemType.APP) return null
    return allApps.find { it.packageName == item.packageName }
}

@Composable
internal fun ManageItemRow(params: ManageItemRowParams) {
    val item = params.item
    val defaultFolderLabel = stringResource(R.string.home_container_edit_folder_label)
    val defaultAppLabel = stringResource(R.string.home_container_edit_app_label)
    val defaultQuickButtonLabel = stringResource(R.string.home_grid_item_quick_button)

    val folder = resolveFolder(item, params.folders)
    val app = resolveApp(item, params.allApps)

    val quickButtonTitle = item.quickButtonModel?.displayTitle(
        params.allApps.find { it.packageName == item.quickButtonModel?.shortcuts?.click?.target }?.label
    )
    val title = when (item.itemType) {
        ContainerItemType.FOLDER -> folder?.name ?: defaultFolderLabel
        ContainerItemType.APP -> app?.label ?: item.packageName ?: defaultAppLabel
        ContainerItemType.QUICK_BUTTON -> quickButtonTitle ?: defaultQuickButtonLabel
    }

    val onOpen = params.onOpen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .then(if (onOpen != null) Modifier.clickable(role = Role.Button, onClick = onOpen) else Modifier)
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        AureoleDS.icons.DragHandle(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )

        ManageItemRowIcon(itemType = item.itemType, app = app, folder = folder)

        Text(
            text = title,
            style = AureoleDS.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleDS.colors.onSurfaceHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        // "›": the folder and the quick button open their editor in place (RF-07); the ✕ badge stays apart.
        if (onOpen != null) {
            AureoleDS.icons.ChevronRight(
                tint = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@Composable
private fun ManageItemRowIcon(
    itemType: ContainerItemType,
    app: AppInfo?,
    folder: AppFolder?
) {
    when (itemType) {
        // The folder as it looks on the home: its icon, letter or emoji over its own color.
        ContainerItemType.FOLDER -> if (folder != null) {
            FolderBadge(folder = folder, size = AureoleDS.dimens.iconMedium)
        } else {
            AureoleDS.icons.Folder(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
        ContainerItemType.APP -> {
            if (app != null) {
                AureoleAppIcon(
                    app = app,
                    size = AureoleDS.dimens.iconMedium,
                    params = AureoleAppIconParams(contentDescription = app.label)
                )
            } else {
                AureoleDS.icons.AppSelect(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        }
        ContainerItemType.QUICK_BUTTON -> {
            AureoleDS.icons.Click(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}
