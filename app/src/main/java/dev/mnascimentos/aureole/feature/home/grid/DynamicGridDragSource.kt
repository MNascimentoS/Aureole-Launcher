@file:Suppress("MatchingDeclarationName", "MagicNumber", "MaxLineLength", "LongMethod")

package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditModifierParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class GridEditModifierArgs(
    val params: GridCellParams,
    val isDragging: Boolean,
    val isResizing: Boolean,
    val onDraggingChange: (Boolean) -> Unit,
    val dragOffset: Offset,
    val onDragOffsetChange: (Offset) -> Unit,
    val animatableOffset: Animatable<Offset, AnimationVector2D>,
    val coroutineScope: CoroutineScope,
    val currentParams: GridCellParams,
    val currentActions: HomeScreenActions,
    val currentOnDragTargetChange: (DragTargetSlot?) -> Unit
)

data class GridCellDragEndParams(
    val params: GridCellParams,
    val dragOffsetX: Float,
    val dragOffsetY: Float,
    val coroutineScope: CoroutineScope,
    val animatableOffset: Animatable<Offset, AnimationVector2D>
)

@Composable
internal fun rememberGridCellEditModifier(args: GridEditModifierArgs): Modifier {
    val item = args.params.item
    val editParams = GridEditModifierParams(
        isEditMode = args.params.isEditMode,
        itemId = item.id,
        isDragging = args.isDragging,
        isResizing = args.isResizing,
        onDragStart = {
            args.onDraggingChange(true)
            args.onDragOffsetChange(Offset.Zero)
            args.coroutineScope.launch { args.animatableOffset.snapTo(Offset.Zero) }
        },
        onDragEnd = {
            handleDragEnd(
                dragParams = GridCellDragEndParams(
                    params = args.currentParams,
                    dragOffsetX = args.dragOffset.x,
                    dragOffsetY = args.dragOffset.y,
                    coroutineScope = args.coroutineScope,
                    animatableOffset = args.animatableOffset
                ),
                onMoveItem = { id, c, r -> args.currentActions.onMoveGridItem(id, c, r) },
                onFinishDrag = {
                    args.onDraggingChange(false)
                    args.onDragOffsetChange(Offset.Zero)
                    args.currentOnDragTargetChange(null)
                }
            )
        },
        onDragCancel = {
            args.onDraggingChange(false)
            args.onDragOffsetChange(Offset.Zero)
            args.currentOnDragTargetChange(null)
        },
        onDrag = { _, amount ->
            val newX = args.dragOffset.x + amount.x
            val newY = args.dragOffset.y + amount.y
            args.onDragOffsetChange(Offset(newX, newY))
            updateDragTargetSlot(args.currentParams, newX, newY, args.currentOnDragTargetChange)
        }
    )
    return Modifier.gridCellEditModifier(editParams)
}

internal fun handleDragEnd(
    dragParams: GridCellDragEndParams,
    onMoveItem: (String, Int, Int) -> Unit,
    onFinishDrag: () -> Unit
) {
    val targetSlot = calculateTargetSlot(
        params = dragParams.params,
        dragOffsetX = dragParams.dragOffsetX,
        dragOffsetY = dragParams.dragOffsetY
    )
    if (targetSlot != null && targetSlot.isValid) {
        onMoveItem(dragParams.params.item.id, targetSlot.col, targetSlot.row)
        onFinishDrag()
    } else {
        dragParams.coroutineScope.launch {
            dragParams.animatableOffset.animateTo(
                targetValue = Offset.Zero,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
            onFinishDrag()
        }
    }
}

internal fun updateDragTargetSlot(
    params: GridCellParams,
    dragOffsetX: Float,
    dragOffsetY: Float,
    onDragTargetChange: (DragTargetSlot?) -> Unit
) {
    val slot = calculateTargetSlot(params, dragOffsetX, dragOffsetY)
    onDragTargetChange(slot)
}

private fun calculateTargetSlot(
    params: GridCellParams,
    dragOffsetX: Float,
    dragOffsetY: Float
): DragTargetSlot? {
    val item = params.item
    val rawTargetCol = (item.col + (dragOffsetX / params.cellWidthPx)).roundToInt()
    val rawTargetRow = (item.row + (dragOffsetY / params.cellHeightPx)).roundToInt()

    val maxCols = params.limits.maxCols
    val maxRows = params.limits.maxRows

    val clampedCol = rawTargetCol.coerceIn(0, (maxCols - item.colSpan).coerceAtLeast(0))
    val clampedRow = rawTargetRow.coerceIn(0, (maxRows - item.rowSpan).coerceAtLeast(0))

    if (clampedCol == item.col && clampedRow == item.row) {
        return null
    }

    val candidate = LauncherItemState(
        id = item.id,
        type = item.type,
        col = clampedCol,
        row = clampedRow,
        colSpan = item.colSpan,
        rowSpan = item.rowSpan
    )
    val isValid = !GridEngineUtils.checkCollisionWithOthers(candidate, params.items) &&
        GridEngineUtils.isWithinBounds(clampedCol, clampedRow, item.colSpan, item.rowSpan, params.limits)

    return DragTargetSlot(
        itemId = item.id,
        col = clampedCol,
        row = clampedRow,
        colSpan = item.colSpan,
        rowSpan = item.rowSpan,
        isValid = isValid
    )
}

@Composable
internal fun Modifier.gridCellEditModifier(params: GridEditModifierParams): Modifier {
    val currentParams by rememberUpdatedState(params)
    return if (params.isEditMode) {
        this
            .border(
                width = 2.dp,
                color = if (params.isDragging || params.isResizing) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(params.itemId) {
                detectDragGestures(
                    onDragStart = { currentParams.onDragStart() },
                    onDragEnd = { currentParams.onDragEnd() },
                    onDragCancel = { currentParams.onDragCancel() },
                    onDrag = { change, amount ->
                        change.consume()
                        currentParams.onDrag(change, amount)
                    }
                )
            }
    } else {
        this
    }
}
