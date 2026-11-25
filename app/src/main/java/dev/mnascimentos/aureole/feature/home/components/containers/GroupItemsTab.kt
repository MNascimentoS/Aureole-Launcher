package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.composable.reorder.ReorderableColumn
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.tips.TipPulse
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupItemRowParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupItemsTabParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetNav
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.components.containers.model.groupItemCount

/**
 * The Items tab (RF-C1, RF-C2): ADD with the three tiles, then IN THE GROUP with the one count of the group and
 * its rows (hold the handle and drag to reorder; TalkBack moves them up or down, RF-C7), or a dashed box when empty.
 */
@Composable
internal fun ColumnScope.GroupItemsTab(params: GroupItemsTabParams) {
    val state = params.state
    val nav = params.nav
    GroupSectionLabel(text = stringResource(R.string.home_container_group_add_label))
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        TipPulse(isActive = params.tip == GroupTip.FIRST_APP, modifier = Modifier.weight(1f).fillMaxHeight()) {
            GroupAddTile(
                title = stringResource(R.string.home_container_group_add_app),
                line = stringResource(R.string.home_container_group_add_app_line),
                icon = { m, c -> AureoleDS.icons.AppSelect(modifier = m, tint = c) },
                onClick = { nav.open(GroupSheetPage.AddApps) }
            )
        }
        GroupAddTile(
            title = stringResource(R.string.home_container_group_add_folder),
            line = stringResource(R.string.home_container_group_add_folder_line),
            icon = { m, c -> AureoleDS.icons.Folder(modifier = m, tint = c) },
            onClick = { nav.open(GroupSheetPage.NewFolder) },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        GroupAddTile(
            title = stringResource(R.string.home_container_group_add_quick_button),
            line = stringResource(R.string.home_container_group_add_quick_button_line),
            icon = { m, c -> AureoleDS.icons.Click(modifier = m, tint = c) },
            onClick = { nav.open(GroupSheetPage.QuickButton) },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
    val count = groupItemCount(state.itemsList)
    GroupSectionLabel(
        text = stringResource(R.string.home_container_group_in_group_label),
        trailing = if (count == 0) {
            stringResource(R.string.home_container_group_item_count_empty)
        } else {
            pluralStringResource(R.plurals.home_container_group_item_count, count, count)
        },
        modifier = Modifier.padding(top = AureoleDS.dimens.large)
    )
    if (state.itemsList.isEmpty()) {
        GroupEmptyBox()
    } else {
        LeftHandMirror { GroupItemList(params) }
        if (state.itemsList.size > 1) {
            AureoleText(
                text = stringResource(R.string.home_container_group_reorder_hint),
                color = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier.fillMaxWidth().padding(top = AureoleDS.dimens.xSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, textAlign = TextAlign.Center)
            )
        }
    }
}

@Composable
private fun GroupItemList(params: GroupItemsTabParams) {
    val state = params.state
    ReorderableColumn(
        params = ReorderableParams(
            items = state.itemsList,
            key = { it.id },
            actions = ReorderableActions(onMove = state::moveItem),
            spacing = AureoleDS.dimens.xSmall,
            dragHandleOnly = true
        ),
        autoScroll = params.autoScroll
    ) { item ->
        val onOpen = groupItemOpener(item, state, params.nav)
        val name = groupItemName(item, state.folders, params.allApps)
        GroupItemRow(
            GroupItemRowParams(
                item = item,
                folders = state.folders,
                allApps = params.allApps,
                onRemove = { params.onRemove(state.itemsList.indexOfFirst { it.id == item.id }, name) },
                onOpen = onOpen
            )
        )
    }
}

/** A small caps section label like the clock sheet's; [trailing], when given, sits at the end of the line. */
@Composable
internal fun GroupSectionLabel(text: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        AureoleMenuSectionLabel(text = text, modifier = Modifier.weight(1f))
        if (trailing != null) {
            AureoleText(
                text = trailing,
                color = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
            )
        }
    }
}

/** A tile of ADD: icon, title and one line of what it adds. */
@Composable
private fun GroupAddTile(
    title: String,
    line: String,
    icon: @Composable (Modifier, Color) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AureoleDS.dimens.shortcutTileHeight)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium), AureoleDS.colors.onSurfaceHigh)
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Medium)
        )
        AureoleText(
            text = line,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, maxLines = 2)
        )
    }
}

@Composable
private fun GroupEmptyBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aureoleDashedBorder(color = AureoleDS.colors.outline)
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = stringResource(R.string.home_container_group_empty),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
        )
    }
}

/** What tapping the row does: opens the folder or quick button editor in place; null for an app (RF-C2). */
private fun groupItemOpener(item: ContainerItemEntity, state: EditContainerState, nav: GroupSheetNav): (() -> Unit)? {
    val folder = state.folders.find { it.id == item.folderId }
    val quickButtonId = item.quickButtonId ?: item.id
    return when {
        folder != null -> ({ nav.openFolder(folder) })
        state.quickButton(quickButtonId) != null ->
            ({ nav.open(GroupSheetPage.QuickButtonEditor(quickButtonId)) })
        else -> null
    }
}
