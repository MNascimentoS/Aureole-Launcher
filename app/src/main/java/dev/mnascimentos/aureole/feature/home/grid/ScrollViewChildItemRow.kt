package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.grid.model.ScrollViewChildParams
import dev.mnascimentos.aureole.feature.home.grid.model.effectiveSpan
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

/**
 * One item of a scroll group, compact (RF-C4): the reorder arrows stacked at the start, then the type icon, the
 * name with edit and remove at the end, and below the name the size control ("Height" or "Width" with "- Nx +"),
 * or "Full list" for the apps list, which has no size. Every button is at least 48 dp and has a description.
 */
@Composable
internal fun ScrollViewChildItemCard(
    params: ScrollViewChildParams,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScrollViewChildReorderArrows(params = params, actions = actions)
        Column(modifier = Modifier.weight(1f)) {
            ScrollViewChildHeaderRow(params = params, actions = actions, onDismissRequest = onDismissRequest)
            ScrollViewChildSizeRow(params = params, actions = actions)
        }
    }
}

@Composable
private fun ScrollViewChildHeaderRow(
    params: ScrollViewChildParams,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    val child = params.child
    val title = getScrollViewChildTitle(child.type)
    val editLabel = stringResource(R.string.home_scrollgroup_edit_item, title)
    val removeLabel = stringResource(R.string.home_scrollgroup_remove_item, title)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                .background(AureoleDS.colors.surface.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            ChildTypeIcon(child.type)
        }
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = AureoleDS.dimens.small),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        )
        TouchButton(
            description = editLabel,
            onClick = { handleEditChildClick(child, actions, onDismissRequest) }
        ) { AureoleDS.icons.Edit(tint = AureoleDS.colors.onSurfaceHigh, modifier = Modifier.size(iconSize())) }
        TouchButton(
            description = removeLabel,
            onClick = { actions.onRemoveChildFromScrollView(params.parentId, child.id) }
        ) { AureoleDS.icons.Delete(tint = AureoleDS.colors.error, modifier = Modifier.size(iconSize())) }
    }
}

@Composable
private fun iconSize() = AureoleDS.dimens.iconMedium

/** A 48 dp button with its own description for TalkBack. */
@Composable
private fun TouchButton(
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(AureoleDS.dimens.minTouchTarget)
            .semantics { contentDescription = description }
    ) { content() }
}

@Composable
private fun ScrollViewChildReorderArrows(params: ScrollViewChildParams, actions: HomeScreenActions) {
    val canMoveUp = params.childIndex > 0
    val canMoveDown = params.childIndex < params.totalChildren - 1
    val disabled = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.4f)
    val enabledTint = AureoleDS.colors.onSurfaceMedium
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TouchButton(
            description = stringResource(R.string.home_scrollgroup_move_up),
            enabled = canMoveUp,
            onClick = { actions.onMoveChildInScrollView(params.parentId, params.child.id, true) }
        ) {
            AureoleDS.icons.ArrowUp(
                tint = if (canMoveUp) enabledTint else disabled,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
        TouchButton(
            description = stringResource(R.string.home_scrollgroup_move_down),
            enabled = canMoveDown,
            onClick = { actions.onMoveChildInScrollView(params.parentId, params.child.id, false) }
        ) {
            AureoleDS.icons.ArrowDown(
                tint = if (canMoveDown) enabledTint else disabled,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@Composable
private fun ScrollViewChildSizeRow(params: ScrollViewChildParams, actions: HomeScreenActions) {
    val span = params.child.effectiveSpan(params.isVertical)
    if (span == null) {
        AureoleText(
            text = stringResource(R.string.home_scrollgroup_full_list),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AureoleDS.dimens.iconXLarge, bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
        return
    }
    val label = stringResource(
        if (params.isVertical) R.string.home_scrollgroup_height else R.string.home_scrollgroup_width
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
        TouchButton(
            description = stringResource(R.string.home_scrollgroup_decrease, label),
            onClick = { adjustChildSpan(params, actions, -1) }
        ) { StepSymbol("-") }
        AureoleText(
            text = "${span}x",
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        )
        TouchButton(
            description = stringResource(R.string.home_scrollgroup_increase, label),
            onClick = { adjustChildSpan(params, actions, 1) }
        ) { StepSymbol("+") }
    }
}

@Composable
private fun StepSymbol(symbol: String, color: Color = AureoleDS.colors.onSurfaceHigh) {
    AureoleText(
        text = symbol,
        color = color,
        format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
    )
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
