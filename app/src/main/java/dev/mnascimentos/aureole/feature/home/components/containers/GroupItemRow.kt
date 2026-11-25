package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.reorder.model.LocalReorderHandle
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.DragHandle
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupItemRowParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.displayTitle

private fun resolveFolder(item: ContainerItemEntity, folders: List<AppFolder>): AppFolder? =
    if (item.itemType == ContainerItemType.FOLDER) folders.find { it.id == item.folderId } else null

private fun resolveApp(item: ContainerItemEntity, allApps: List<AppInfo>): AppInfo? =
    if (item.itemType == ContainerItemType.APP) allApps.find { it.packageName == item.packageName } else null

/** The name of [item] as the list and the "removed" snackbar call it. */
@Composable
internal fun groupItemName(item: ContainerItemEntity, folders: List<AppFolder>, allApps: List<AppInfo>): String =
    when (item.itemType) {
        ContainerItemType.FOLDER -> resolveFolder(item, folders)?.name
            ?: stringResource(R.string.home_container_group_add_folder)
        ContainerItemType.APP -> resolveApp(item, allApps)?.label ?: item.packageName
            ?: stringResource(R.string.home_container_group_type_app)
        ContainerItemType.QUICK_BUTTON -> item.quickButtonModel?.displayTitle(
            allApps.find { it.packageName == item.quickButtonModel?.shortcuts?.click?.target }?.label
        ) ?: stringResource(R.string.home_container_group_type_quick_button)
    }

/**
 * One row of "In the group" (RF-C2): the drag handle (hold and drag), the icon, the name and what it is ("App",
 * "Folder · 4 apps", "Quick button"), and the × inside the row, 48 dp, "Remove <name>". A folder or a quick button
 * opens its editor on tap.
 */
@Composable
internal fun GroupItemRow(params: GroupItemRowParams) {
    val item = params.item
    val folder = resolveFolder(item, params.folders)
    val app = resolveApp(item, params.allApps)
    val name = groupItemName(item, params.folders, params.allApps)
    val onOpen = params.onOpen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(color = AureoleDS.colors.surfaceVariant.copy(alpha = ROW_ALPHA))
            .then(if (onOpen != null) Modifier.clickable(role = Role.Button, onClick = onOpen) else Modifier)
            .padding(start = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        GroupItemDragHandle()
        GroupItemIcon(itemType = item.itemType, app = app, folder = folder)
        Column(modifier = Modifier.weight(1f).padding(vertical = AureoleDS.dimens.xSmall)) {
            AureoleText(
                text = name,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
            AureoleText(
                text = groupItemType(item.itemType, folder),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, maxLines = 1)
            )
        }
        val removeLabel = stringResource(R.string.home_container_group_remove, name)
        IconButton(
            onClick = params.onRemove,
            modifier = Modifier
                .size(AureoleDS.dimens.minTouchTarget)
                .semantics { contentDescription = removeLabel }
        ) {
            AureoleDS.icons.Close(tint = AureoleDS.colors.onSurfaceMedium, modifier = Modifier.size(AureoleDS.dimens.iconSmall))
        }
    }
}

/** The handle that starts the drag (hold, then drag): the reorderable list gives it its gesture (RF-C7). */
@Composable
private fun GroupItemDragHandle() {
    val handle = LocalReorderHandle.current
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.iconLarge, AureoleDS.dimens.minTouchTarget)
            .then(handle.gestureModifier),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.DragHandle(tint = AureoleDS.colors.onSurfaceMedium, modifier = Modifier.size(AureoleDS.dimens.iconSmall))
    }
}

/** "App", "Folder · 4 apps" or "Quick button". */
@Composable
@ReadOnlyComposable
private fun groupItemType(itemType: ContainerItemType, folder: AppFolder?): String = when (itemType) {
    ContainerItemType.APP -> stringResource(R.string.home_container_group_type_app)
    ContainerItemType.FOLDER -> {
        val count = folder?.appPackageNames?.size ?: 0
        pluralStringResource(R.plurals.home_container_group_type_folder, count, count)
    }
    ContainerItemType.QUICK_BUTTON -> stringResource(R.string.home_container_group_type_quick_button)
}

/** The app's icon; a folder and a quick button show their glyph over the outline color. */
@Composable
private fun GroupItemIcon(itemType: ContainerItemType, app: AppInfo?, folder: AppFolder?) {
    if (itemType == ContainerItemType.APP && app != null) {
        AureoleAppIcon(
            app = app,
            size = AureoleDS.dimens.xLarge,
            params = AureoleAppIconParams(contentDescription = null)
        )
        return
    }
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xLarge)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(AureoleDS.colors.outline),
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = Modifier.size(AureoleDS.dimens.iconMedium)
        val tint = AureoleDS.colors.onSurfaceHigh
        when {
            itemType == ContainerItemType.FOLDER && folder != null -> AureoleDS.icons.Folder(tint = tint, modifier = iconModifier)
            itemType == ContainerItemType.QUICK_BUTTON -> AureoleDS.icons.Click(tint = tint, modifier = iconModifier)
            else -> AureoleDS.icons.AppSelect(tint = tint, modifier = iconModifier)
        }
    }
}

private const val ROW_ALPHA = 0.3f

@AureolePreview
@Composable
private fun GroupItemRowPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            GroupItemRow(
                GroupItemRowParams(
                    item = ContainerItemEntity(
                        id = "preview",
                        panelId = "preview",
                        itemType = ContainerItemType.FOLDER,
                        folderId = "folder",
                        orderIndex = 0
                    ),
                    folders = listOf(AppFolder(id = "folder", name = "Social", appPackageNames = listOf("a", "b"))),
                    allApps = emptyList(),
                    onRemove = {}
                )
            )
        }
    }
}
