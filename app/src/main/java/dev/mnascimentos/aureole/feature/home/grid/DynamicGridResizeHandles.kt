package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.grid.model.CornerResizeCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import dev.mnascimentos.aureole.feature.home.grid.model.ResizeHandleCallbacks
import kotlin.math.roundToInt

private val HANDLE_TOUCH_SIZE = 28.dp
private val CORNER_TOUCH_SIZE = 36.dp
private val CORNER_HANDLE_RADIUS = 12.dp

@Composable
fun BoxScope.RightEdgeResizeHandle(
    params: GridCellParams,
    callbacks: ResizeHandleCallbacks
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .width(HANDLE_TOUCH_SIZE)
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
    callbacks: ResizeHandleCallbacks
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(HANDLE_TOUCH_SIZE)
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
    callbacks: CornerResizeCallbacks
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
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(CORNER_TOUCH_SIZE)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = CORNER_HANDLE_RADIUS)
            )
            .pointerInput(params.item.id) { detectTapGestures(onTap = { currentCallbacks.onEditItem() }) }
            .then(dragModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar Container",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
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
