package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
 * Vertical list whose items move by holding and dragging them up or down; each item fills the width. Stateless:
 * apply every [ReorderableActions.onMove] to the items you pass back. It does not scroll by itself, so it fits
 * inside a scrolling sheet; [trailingContent] (for example an "add" row) follows the items and does not move.
 */
@Composable
fun <T> ReorderableColumn(
    params: ReorderableParams<T>,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    itemContent: @Composable (item: T) -> Unit,
) {
    val state = remember { ReorderDragState() }
    val spacingPx = with(LocalDensity.current) { params.spacing.toPx() }
    val keys = params.items.map(params.key)
    // Room above the first item for its ✕ badge, which sticks out of the top end corner; a parent scroll would
    // clip it otherwise. The badge's sideways overhang stays within the clip margin scroll containers allow.
    val badgeRoom = if (params.actions.onRemove != null) reorderBadgeOverhang() else AureoleDS.dimens.zero
    Column(
        modifier = modifier
            .padding(params.contentPadding)
            .padding(top = badgeRoom),
        verticalArrangement = Arrangement.spacedBy(params.spacing)
    ) {
        params.items.forEachIndexed { index, item ->
            key(keys[index]) {
                ReorderableSlot(
                    slot = ReorderSlotParams(
                        keys[index],
                        index,
                        keys,
                        ReorderOrientation.Vertical,
                        spacingPx,
                        params.dragHandleOnly
                    ),
                    state = state,
                    actions = params.actions,
                    modifier = Modifier.fillMaxWidth()
                ) { itemContent(item) }
            }
        }
        trailingContent?.invoke()
    }
}

@Composable
private fun ReorderableColumnSample(isLeftHanded: Boolean) {
    var items by remember { mutableStateOf(List(PREVIEW_ITEM_COUNT) { it + 1 }) }
    AureoleLauncherTheme {
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            ReorderableColumn(
                params = ReorderableParams(
                    items = items,
                    key = { it },
                    actions = ReorderableActions(
                        onMove = { from, to -> items = move(items, from, to) },
                        onRemove = { index -> items = items.filterIndexed { i, _ -> i != index } }
                    ),
                    spacing = AureoleDS.dimens.xSmall,
                    contentPadding = PaddingValues(AureoleDS.dimens.medium)
                )
            ) { item -> SampleRow(text = item.toString()) }
        }
    }
}

@Composable
private fun SampleRow(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.itemHeightExtraLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = AureoleDS.colors.onSurfaceHigh, style = AureoleDS.typography.titleMedium)
    }
}

@AureolePreview
@Composable
private fun ReorderableColumnPreview() = ReorderableColumnSample(isLeftHanded = false)

@AureolePreview
@Composable
private fun ReorderableColumnLeftHandedPreview() = ReorderableColumnSample(isLeftHanded = true)
