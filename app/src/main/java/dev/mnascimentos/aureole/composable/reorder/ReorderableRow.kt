package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.ReorderDragState
import dev.mnascimentos.aureole.composable.reorder.model.ReorderOrientation
import dev.mnascimentos.aureole.composable.reorder.model.ReorderSlotParams
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val PREVIEW_ITEM_COUNT = 3

/**
 * Horizontal list whose items move by holding and dragging them sideways. Stateless: apply every
 * [ReorderableActions.onMove] to the items you pass back. Scrolls when the items overflow (not while one is held);
 * [trailingContent] (for example an "add" card) follows the items and does not move. Follows the layout direction,
 * so wrap it in `LeftHandMirror` to run it from the right.
 */
@Composable
fun <T> ReorderableRow(
    params: ReorderableParams<T>,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    trailingContent: (@Composable () -> Unit)? = null,
    itemContent: @Composable (item: T) -> Unit,
) {
    val state = remember { ReorderDragState() }
    val spacingPx = with(LocalDensity.current) { params.spacing.toPx() }
    val keys = params.items.map(params.key)
    // Room inside the scroll for the ✕ badge that sticks out of the top end corner of each item.
    val badgeRoom = if (params.actions.onRemove != null) reorderBadgeOverhang() else AureoleDS.dimens.zero
    Row(
        modifier = modifier
            .horizontalScroll(scrollState, enabled = !state.isDragging)
            .padding(params.contentPadding)
            .padding(top = badgeRoom, end = badgeRoom),
        horizontalArrangement = Arrangement.spacedBy(params.spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        params.items.forEachIndexed { index, item ->
            key(keys[index]) {
                ReorderableSlot(
                    slot = ReorderSlotParams(keys[index], index, keys, ReorderOrientation.Horizontal, spacingPx),
                    state = state,
                    actions = params.actions
                ) { itemContent(item) }
            }
        }
        trailingContent?.invoke()
    }
}

@Composable
private fun ReorderableRowSample(isLeftHanded: Boolean) {
    var items by remember { mutableStateOf(List(PREVIEW_ITEM_COUNT) { it + 1 }) }
    AureoleLauncherTheme {
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            ReorderableRow(
                params = ReorderableParams(
                    items = items,
                    key = { it },
                    actions = ReorderableActions(
                        onMove = { from, to -> items = move(items, from, to) },
                        onRemove = { index -> items = items.filterIndexed { i, _ -> i != index } }
                    ),
                    spacing = AureoleDS.dimens.xSmall,
                    contentPadding = PaddingValues(AureoleDS.dimens.medium)
                ),
                trailingContent = { SampleCard(text = "+") }
            ) { item -> SampleCard(text = item.toString()) }
        }
    }
}

@Composable
private fun SampleCard(text: String) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.sizeLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = AureoleDS.colors.onSurfaceHigh, style = AureoleDS.typography.titleMedium)
    }
}

@AureolePreview
@Composable
private fun ReorderableRowPreview() = ReorderableRowSample(isLeftHanded = false)

@AureolePreview
@Composable
private fun ReorderableRowLeftHandedPreview() = ReorderableRowSample(isLeftHanded = true)
