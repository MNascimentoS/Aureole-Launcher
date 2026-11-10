package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.grid.model.ScrollViewChildParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
fun SingleScrollViewChildRow(
    parentId: String,
    child: LauncherItemState,
    isVertical: Boolean,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    ScrollViewChildItemCard(
        params = ScrollViewChildParams(
            parentId = parentId,
            child = child,
            childIndex = 0,
            totalChildren = 1,
            isVertical = isVertical
        ),
        actions = actions,
        onDismissRequest = onDismissRequest
    )
}

@Composable
internal fun ScrollViewChildItemCard(
    params: ScrollViewChildParams,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
    ) {
        ScrollViewChildHeaderRow(
            child = params.child,
            parentId = params.parentId,
            actions = actions,
            onDismissRequest = onDismissRequest
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))

        ScrollViewChildReorderAndResizeControls(
            params = params,
            actions = actions
        )
    }
}

@Composable
private fun ScrollViewChildHeaderRow(
    child: LauncherItemState,
    parentId: String,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    val title = getScrollViewChildTitle(child.type)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                .background(AureoleDS.colors.surface.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            ChildTypeIcon(child.type)
        }

        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))

        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        )

        IconButton(
            onClick = { handleEditChildClick(child, actions, onDismissRequest) },
            modifier = Modifier.size(AureoleDS.dimens.xLarge)
        ) {
            AureoleDS.icons.Edit(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))

        IconButton(
            onClick = { actions.onRemoveChildFromScrollView(parentId, child.id) },
            modifier = Modifier.size(AureoleDS.dimens.xLarge)
        ) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.error,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}

@Composable
private fun ScrollViewChildReorderAndResizeControls(
    params: ScrollViewChildParams,
    actions: HomeScreenActions
) {
    val childIndex = params.childIndex
    val totalChildren = params.totalChildren
    val child = params.child
    val parentId = params.parentId

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
        ) {
            IconButton(
                onClick = { actions.onMoveChildInScrollView(parentId, child.id, true) },
                enabled = (childIndex > 0),
                modifier = Modifier.size(AureoleDS.dimens.large)
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = if (childIndex > 0) {
                        AureoleDS.colors.onSurfaceHigh
                    } else {
                        AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.4f)
                    },
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }

            IconButton(
                onClick = { actions.onMoveChildInScrollView(parentId, child.id, false) },
                enabled = (childIndex < totalChildren - 1),
                modifier = Modifier.size(AureoleDS.dimens.large)
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = if (childIndex < totalChildren - 1) {
                        AureoleDS.colors.onSurfaceHigh
                    } else {
                        AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.4f)
                    },
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }

        ScrollViewChildResizeControls(params = params, actions = actions)
    }
}

@Composable
private fun ScrollViewChildResizeControls(
    params: ScrollViewChildParams,
    actions: HomeScreenActions
) {
    val child = params.child
    val isVertical = params.isVertical
    val spanVal = if (isVertical) child.rowSpan else child.colSpan

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(AureoleDS.colors.surface.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.minimal)
    ) {
        IconButton(
            onClick = { adjustChildSpan(params, actions, -1) },
            modifier = Modifier.size(AureoleDS.dimens.large)
        ) {
            AureoleText(
                text = "-",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        }

        AureoleText(
            text = "${spanVal}x",
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        )

        IconButton(
            onClick = { adjustChildSpan(params, actions, 1) },
            modifier = Modifier.size(AureoleDS.dimens.large)
        ) {
            AureoleText(
                text = "+",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun adjustChildSpan(params: ScrollViewChildParams, actions: HomeScreenActions, delta: Int) {
    val child = params.child
    val parentId = params.parentId
    if (params.isVertical) {
        val newSpan = (child.rowSpan + delta).coerceAtLeast(1)
        actions.onResizeChildInScrollView(parentId, child.id, child.colSpan, newSpan)
    } else {
        val newSpan = (child.colSpan + delta).coerceAtLeast(1)
        actions.onResizeChildInScrollView(parentId, child.id, newSpan, child.rowSpan)
    }
}

private fun handleEditChildClick(
    child: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    when (child.type) {
        LauncherItemType.CLOCK -> {
            actions.onOpenEditClockBottomSheet()
            onDismissRequest()
        }
        LauncherItemType.SHORTCUTS_CONTAINER,
        LauncherItemType.SCROLL_VIEW -> {
            actions.onOpenEditContainerDialog(child.id)
            onDismissRequest()
        }
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SINGLE_APP_WIDGET -> {
            actions.onOpenWidgetStackBottomSheet(null, child.id)
            onDismissRequest()
        }
        LauncherItemType.APPS_LIST -> {
            actions.onOpenFavoritePicker(child.id)
            onDismissRequest()
        }
        LauncherItemType.ALL_APPS_LIST -> {
            // No edit action for all apps list
        }
        LauncherItemType.QUICK_BUTTON -> {
            actions.onOpenQuickButtonSettings(child.id)
            onDismissRequest()
        }
        else -> {}
    }
}

@Composable
private fun ChildTypeIcon(type: LauncherItemType?) {
    val iconModifier = Modifier.size(AureoleDS.dimens.iconMedium)
    when (type) {
        LauncherItemType.CLOCK -> AureoleDS.icons.Add(iconModifier)
        LauncherItemType.APPS_LIST, LauncherItemType.ALL_APPS_LIST -> AureoleDS.icons.HorizontalContainer(iconModifier)
        LauncherItemType.SHORTCUTS_CONTAINER -> AureoleDS.icons.MenuIcon(iconModifier)
        LauncherItemType.WIDGET_LIST -> AureoleDS.icons.Add(iconModifier)
        LauncherItemType.SCROLL_VIEW -> AureoleDS.icons.VerticalContainer(iconModifier)
        LauncherItemType.QUICK_BUTTON -> AureoleDS.icons.Click(iconModifier)
        else -> AureoleDS.icons.Add(iconModifier)
    }
}

@Composable
@ReadOnlyComposable
private fun getScrollViewChildTitle(type: LauncherItemType?): String {
    return when (type) {
        LauncherItemType.CLOCK -> stringResource(R.string.home_container_add_clock)
        LauncherItemType.APPS_LIST -> stringResource(R.string.home_container_add_apps_list)
        LauncherItemType.ALL_APPS_LIST -> stringResource(R.string.home_container_add_all_apps_list)
        LauncherItemType.SHORTCUTS_CONTAINER -> stringResource(R.string.home_grid_item_container)
        LauncherItemType.WIDGET_LIST -> stringResource(R.string.home_grid_item_widget_group)
        LauncherItemType.SCROLL_VIEW -> stringResource(R.string.home_container_add_scroll_view)
        LauncherItemType.QUICK_BUTTON -> stringResource(R.string.home_container_add_quick_button)
        else -> stringResource(R.string.home_grid_scroll_component)
    }
}
