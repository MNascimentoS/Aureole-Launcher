package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.grid.model.CornerResizeCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import dev.mnascimentos.aureole.feature.home.grid.model.ResizeHandleCallbacks
import kotlin.math.roundToInt

@Composable
fun BoxScope.RightEdgeResizeHandle(
    params: GridCellParams,
    callbacks: ResizeHandleCallbacks,
    modifier: Modifier = Modifier
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    Box(
        modifier = modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .padding(top = AureoleDS.dimens.iconXLarge, bottom = AureoleDS.dimens.iconXLarge)
            .width(AureoleDS.dimens.iconXLargeMedium)
            .pointerInput(params.item.id) {
                var totalDragX = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        currentCallbacks.onResizing(true)
                    },
                    onDragEnd = {
                        currentCallbacks.onResizing(false)
                        val item = currentItem
                        val p = currentParams
                        val currW = (item.colSpan * p.cellWidthPx) + totalDragX
                        val targetColSpan = (currW / p.cellWidthPx).roundToInt().coerceIn(
                            item.minColSpan,
                            p.limits.maxCols - item.col
                        )
                        val resizedItem = item.copy(colSpan = targetColSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, p.items)
                        currentCallbacks.onResetExtra()
                        if (!hasCollision && targetColSpan != item.colSpan) {
                            currentCallbacks.onResizeItem(item.id, targetColSpan, item.rowSpan)
                        }
                    },
                    onDragCancel = {
                        currentCallbacks.onResizing(false)
                        currentCallbacks.onResetExtra()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        currentCallbacks.onDelta(dragAmount.x)
                    }
                )
            }
    )
}

@Composable
fun BoxScope.BottomEdgeResizeHandle(
    params: GridCellParams,
    callbacks: ResizeHandleCallbacks,
    modifier: Modifier = Modifier
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    Box(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(start = AureoleDS.dimens.iconXLarge, end = AureoleDS.dimens.iconXLarge)
            .height(AureoleDS.dimens.iconXLargeMedium)
            .pointerInput(params.item.id) {
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragY = 0f
                        currentCallbacks.onResizing(true)
                    },
                    onDragEnd = {
                        currentCallbacks.onResizing(false)
                        val item = currentItem
                        val p = currentParams
                        val currH = (item.rowSpan * p.cellHeightPx) + totalDragY
                        val targetRowSpan = (currH / p.cellHeightPx).roundToInt().coerceIn(
                            item.minRowSpan,
                            p.limits.maxRows - item.row
                        )
                        val resizedItem = item.copy(rowSpan = targetRowSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, p.items)
                        currentCallbacks.onResetExtra()
                        if (!hasCollision && targetRowSpan != item.rowSpan) {
                            currentCallbacks.onResizeItem(item.id, item.colSpan, targetRowSpan)
                        }
                    },
                    onDragCancel = {
                        currentCallbacks.onResizing(false)
                        currentCallbacks.onResetExtra()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragY += dragAmount.y
                        currentCallbacks.onDelta(dragAmount.y)
                    }
                )
            }
    )
}

@Composable
fun BoxScope.CornerResizeHandle(
    params: GridCellParams,
    callbacks: CornerResizeCallbacks,
    modifier: Modifier = Modifier
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    val dragModifier = Modifier.pointerInput(params.item.id) {
        var totalDragX = 0f
        var totalDragY = 0f
        detectDragGestures(
            onDragStart = {
                totalDragX = 0f
                totalDragY = 0f
                currentCallbacks.onResizing(true)
            },
            onDragEnd = {
                handleCornerDragEnd(
                    item = currentItem,
                    params = currentParams,
                    callbacks = currentCallbacks,
                    totalDragX = totalDragX,
                    totalDragY = totalDragY
                )
            },
            onDragCancel = {
                currentCallbacks.onResizing(false)
                currentCallbacks.onResetExtra()
            },
            onDrag = { change, dragAmount ->
                change.consume()
                totalDragX += dragAmount.x
                totalDragY += dragAmount.y
                currentCallbacks.onDelta(dragAmount.x, dragAmount.y)
            }
        )
    }

    Box(
        modifier = modifier
            .align(Alignment.BottomEnd)
            .size(AureoleDS.dimens.iconXLarge)
            .background(
                color = AureoleDS.colors.surfaceVariant,
                shape = RoundedCornerShape(topStart = AureoleDS.dimens.radiusSmall)
            )
            .pointerInput(params.item.id) { detectTapGestures(onTap = { currentCallbacks.onEditItem() }) }
            .then(dragModifier),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Edit(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}

private fun handleCornerDragEnd(
    item: LauncherItemState,
    params: GridCellParams,
    callbacks: CornerResizeCallbacks,
    totalDragX: Float,
    totalDragY: Float
) {
    callbacks.onResizing(false)
    val currW = (item.colSpan * params.cellWidthPx) + totalDragX
    val currH = (item.rowSpan * params.cellHeightPx) + totalDragY
    val targetColSpan = (currW / params.cellWidthPx).roundToInt().coerceIn(
        item.minColSpan,
        params.limits.maxCols - item.col
    )
    val targetRowSpan = (currH / params.cellHeightPx).roundToInt().coerceIn(
        item.minRowSpan,
        params.limits.maxRows - item.row
    )
    val resizedItem = item.copy(colSpan = targetColSpan, rowSpan = targetRowSpan)
    val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, params.items)
    callbacks.onResetExtra()
    val colSpanChanged = targetColSpan != item.colSpan
    val rowSpanChanged = targetRowSpan != item.rowSpan
    if (!hasCollision && (colSpanChanged || rowSpanChanged)) {
        callbacks.onResizeItem(item.id, targetColSpan, targetRowSpan)
    }
}

@Composable
fun BoxScope.DeleteActionHandle(
    itemId: String,
    onDeleteItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnDelete by rememberUpdatedState(onDeleteItem)

    Box(
        modifier = modifier
            .align(Alignment.TopEnd)
            .size(AureoleDS.dimens.iconXLarge)
            .background(
                color = AureoleDS.colors.error,
                shape = RoundedCornerShape(bottomStart = AureoleDS.dimens.radiusSmall)
            )
            .pointerInput(itemId + "_delete") { detectTapGestures(onTap = { currentOnDelete() }) },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Delete(
            tint = AureoleDS.colors.onError,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}
