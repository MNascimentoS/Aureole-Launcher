package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil

private val HANDLE_TOUCH_SIZE = 28.dp
private val CORNER_TOUCH_SIZE = 36.dp
private val BORDER_CORNER_RADIUS = 16.dp
private val CORNER_HANDLE_RADIUS = 12.dp
private const val GRID_OFFSET_PX = 8f
private const val GRID_MARGIN_PX = 16f
private const val GRID_LINE_WIDTH = 2f
private const val GRID_CORNER_RADIUS = 24f
private const val DASH_LENGTH_PX = 10f
private const val RESIZE_SNAP_EPSILON = 0.05f

data class GridEditConfig(
    val isEditMode: Boolean,
    val limits: GridLimits = GridLimits()
)

private data class CellDragCallbacks(
    val onMove: (String, Int, Int) -> Unit,
    val onReset: () -> Unit,
    val onAnimate: () -> Unit
)

private data class ResizeHandleCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onDelta: (Float) -> Unit,
    val onResetExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit
)

private data class CornerResizeCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onDelta: (Float, Float) -> Unit,
    val onResetExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit,
    val onEditItem: () -> Unit
)

private data class GridItemEditCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onResizeWidthDelta: (Float) -> Unit,
    val onResizeHeightDelta: (Float) -> Unit,
    val onResizeDelta: (Float, Float) -> Unit,
    val onResetWidthExtra: () -> Unit,
    val onResetHeightExtra: () -> Unit,
    val onResetAllExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit,
    val onEditItem: () -> Unit
)

private data class GridEditModifierParams(
    val isEditMode: Boolean,
    val itemId: String,
    val isDragging: Boolean,
    val isResizing: Boolean,
    val onDragStart: () -> Unit,
    val onDragEnd: () -> Unit,
    val onDragCancel: () -> Unit,
    val onDrag: (PointerInputChange, Offset) -> Unit
)

private data class CellBoxCallbacks(
    val onResizingChange: (Boolean) -> Unit,
    val onResizeWidthChange: (Float) -> Unit,
    val onResizeHeightChange: (Float) -> Unit,
    val onResizeDelta: (Float, Float) -> Unit,
    val onResetWidth: () -> Unit,
    val onResetHeight: () -> Unit,
    val onResetAll: () -> Unit
)

private data class CellBoxMetrics(
    val leftDp: Dp,
    val topDp: Dp,
    val currentWidthDp: Dp,
    val currentHeightDp: Dp,
    val totalDragX: Float,
    val totalDragY: Float
)

private data class CellBoxConfig(
    val metrics: CellBoxMetrics,
    val modifier: Modifier,
    val params: GridCellParams,
    val callbacks: CellBoxCallbacks,
    val actions: HomeScreenActions,
    val content: @Composable () -> Unit
)

data class GridCellParams(
    val item: LauncherItemState,
    val items: List<LauncherItemState>,
    val isEditMode: Boolean,
    val cellWidthPx: Float,
    val cellHeightPx: Float,
    val cellWidthDp: Dp,
    val cellHeightDp: Dp,
    val limits: GridLimits = GridLimits()
)

@Composable
fun DynamicGridContainer(
    items: List<LauncherItemState>,
    config: GridEditConfig,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: LauncherItemState) -> Unit
) {
    val isEditMode = config.isEditMode
    val limits = config.limits
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isEditMode) {
                detectTapGestures(
                    onLongPress = {
                        if (!isEditMode) actions.onEnterGridEditMode()
                    }
                )
            }
    ) {
        val density = LocalDensity.current
        val wPx = with(density) { constraints.maxWidth.toDp().toPx() }
        val hPx = with(density) { constraints.maxHeight.toDp().toPx() }
        val cellWidthPx = wPx / limits.maxCols
        val cellHeightPx = hPx / limits.maxRows
        val cellWidthDp = with(density) { cellWidthPx.toDp() }
        val cellHeightDp = with(density) { cellHeightPx.toDp() }

        if (isEditMode) {
            GridBackgroundOverlay(limits = limits, cellWidthPx = cellWidthPx, cellHeightPx = cellHeightPx)
        }

        items.forEach { item ->
            val params = GridCellParams(
                item = item,
                items = items,
                isEditMode = isEditMode,
                cellWidthPx = cellWidthPx,
                cellHeightPx = cellHeightPx,
                cellWidthDp = cellWidthDp,
                cellHeightDp = cellHeightDp,
                limits = limits
            )
            GridItemCell(params = params, actions = actions, content = { itemContent(item) })
        }

        if (isEditMode) {
            GridTopEditBar(
                onSave = actions.onSaveGridEditMode,
                onCancel = actions.onCancelGridEditMode
            )
            FloatingActionButton(
                onClick = actions.onOpenAddContainerDialog,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Container")
            }
        }
    }
}

@Composable
private fun GridTopEditBar(onSave: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onCancel,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancelar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Cancelar", maxLines = 1)
        }

        Button(
            onClick = onSave,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Salvar", maxLines = 1)
        }
    }
}

@Composable
private fun GridBackgroundOverlay(
    limits: GridLimits,
    cellWidthPx: Float,
    cellHeightPx: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val stroke = Stroke(
            width = GRID_LINE_WIDTH,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_LENGTH_PX, DASH_LENGTH_PX), 0f)
        )
        val gridColor = Color.White.copy(alpha = 0.25f)
        for (col in 0 until limits.maxCols) {
            for (row in 0 until limits.maxRows) {
                drawRoundRect(
                    color = gridColor,
                    topLeft = Offset(col * cellWidthPx + GRID_OFFSET_PX, row * cellHeightPx + GRID_OFFSET_PX),
                    size = Size(cellWidthPx - GRID_MARGIN_PX, cellHeightPx - GRID_MARGIN_PX),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS),
                    style = stroke
                )
            }
        }
    }
}

@Composable
private fun GridItemCell(
    params: GridCellParams,
    actions: HomeScreenActions,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val item = params.item
    var resizeExtraWidthPx by remember { mutableFloatStateOf(0f) }
    var resizeExtraHeightPx by remember { mutableFloatStateOf(0f) }
    var isResizing by remember { mutableStateOf(false) }
    val animatableOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val density = LocalDensity.current
    val currentWidthDp = (params.cellWidthDp * item.colSpan) + with(density) { resizeExtraWidthPx.toDp() }
    val currentHeightDp = (params.cellHeightDp * item.rowSpan) + with(density) { resizeExtraHeightPx.toDp() }
    val leftDp = params.cellWidthDp * item.col
    val topDp = params.cellHeightDp * item.row
    val totalDragX = animatableOffset.value.x
    val totalDragY = animatableOffset.value.y

    val editModifier = rememberGridEditModifier(params, coroutineScope, animatableOffset, actions) { isResizing = it }

    GridItemCellBox(
        CellBoxConfig(
            metrics = CellBoxMetrics(leftDp, topDp, currentWidthDp, currentHeightDp, totalDragX, totalDragY),
            modifier = editModifier,
            params = params,
            callbacks = CellBoxCallbacks(
                onResizingChange = { isResizing = it },
                onResizeWidthChange = { resizeExtraWidthPx += it },
                onResizeHeightChange = { resizeExtraHeightPx += it },
                onResizeDelta = { dx, dy -> resizeExtraWidthPx += dx; resizeExtraHeightPx += dy },
                onResetWidth = { resizeExtraWidthPx = 0f },
                onResetHeight = { resizeExtraHeightPx = 0f },
                onResetAll = { resizeExtraWidthPx = 0f; resizeExtraHeightPx = 0f }
            ),
            actions = actions,
            content = content
        )
    )
}

@Composable
private fun rememberGridEditModifier(
    params: GridCellParams,
    coroutineScope: CoroutineScope,
    animatableOffset: Animatable<Offset, AnimationVector2D>,
    actions: HomeScreenActions,
    onResizingChange: (Boolean) -> Unit
): Modifier {
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    return Modifier.gridCellEditModifier(
        GridEditModifierParams(
            isEditMode = params.isEditMode,
            itemId = params.item.id,
            isDragging = isDragging,
            isResizing = false,
            onDragStart = { isDragging = true; onResizingChange(true) },
            onDragEnd = {
                isDragging = false
                onResizingChange(false)
                handleCellDragEnd(
                    params = params,
                    dragOffsetX = dragOffsetX,
                    dragOffsetY = dragOffsetY,
                    callbacks = CellDragCallbacks(
                        onMove = actions.onMoveGridItem,
                        onReset = { dragOffsetX = 0f; dragOffsetY = 0f },
                        onAnimate = {
                            coroutineScope.launch {
                                animatableOffset.snapTo(Offset(dragOffsetX, dragOffsetY))
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                                animatableOffset.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    )
                )
            },
            onDragCancel = { isDragging = false; onResizingChange(false); dragOffsetX = 0f; dragOffsetY = 0f },
            onDrag = { _, dragAmount ->
                dragOffsetX += dragAmount.x
                dragOffsetY += dragAmount.y
            }
        )
    )
}

@Composable
private fun GridItemCellBox(config: CellBoxConfig) {
    val metrics = config.metrics
    val params = config.params
    val item = params.item
    Box(
        modifier = Modifier
            .offset(x = metrics.leftDp, y = metrics.topDp)
            .size(width = metrics.currentWidthDp, height = metrics.currentHeightDp)
            .graphicsLayer { translationX = metrics.totalDragX; translationY = metrics.totalDragY }
            .padding(4.dp)
            .then(config.modifier)
    ) {
        config.content()

        if (params.isEditMode) {
            GridItemEditOverlay(
                params = params,
                callbacks = GridItemEditCallbacks(
                    onResizing = config.callbacks.onResizingChange,
                    onResizeWidthDelta = config.callbacks.onResizeWidthChange,
                    onResizeHeightDelta = config.callbacks.onResizeHeightChange,
                    onResizeDelta = config.callbacks.onResizeDelta,
                    onResetWidthExtra = config.callbacks.onResetWidth,
                    onResetHeightExtra = config.callbacks.onResetHeight,
                    onResetAllExtra = config.callbacks.onResetAll,
                    onResizeItem = config.actions.onResizeGridItem,
                    onEditItem = { config.actions.onOpenEditContainerDialog(item) }
                )
            )
        }
    }
}

@Composable
private fun Modifier.gridCellEditModifier(params: GridEditModifierParams): Modifier {
    return if (params.isEditMode) {
        this
            .border(
                width = 2.dp,
                color = if (params.isDragging || params.isResizing) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(BORDER_CORNER_RADIUS)
            )
            .clip(RoundedCornerShape(BORDER_CORNER_RADIUS))
            .pointerInput(params.itemId) {
                detectDragGestures(
                    onDragStart = { params.onDragStart() },
                    onDragEnd = { params.onDragEnd() },
                    onDragCancel = { params.onDragCancel() },
                    onDrag = { change, amount ->
                        change.consume()
                        params.onDrag(change, amount)
                    }
                )
            }
    } else {
        this
    }
}

@Composable
private fun BoxScope.GridItemEditOverlay(
    params: GridCellParams,
    callbacks: GridItemEditCallbacks
) {
    val item = params.item
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(item.id + "_click_blocker") {
                detectTapGestures(onTap = {})
            }
    )

    RightEdgeResizeHandle(
        params = params,
        callbacks = ResizeHandleCallbacks(
            onResizing = callbacks.onResizing,
            onDelta = callbacks.onResizeWidthDelta,
            onResetExtra = callbacks.onResetWidthExtra,
            onResizeItem = callbacks.onResizeItem
        )
    )

    BottomEdgeResizeHandle(
        params = params,
        callbacks = ResizeHandleCallbacks(
            onResizing = callbacks.onResizing,
            onDelta = callbacks.onResizeHeightDelta,
            onResetExtra = callbacks.onResetHeightExtra,
            onResizeItem = callbacks.onResizeItem
        )
    )

    CornerResizeHandle(
        params = params,
        callbacks = CornerResizeCallbacks(
            onResizing = callbacks.onResizing,
            onDelta = callbacks.onResizeDelta,
            onResetExtra = callbacks.onResetAllExtra,
            onResizeItem = callbacks.onResizeItem,
            onEditItem = callbacks.onEditItem
        )
    )
}

private fun handleCellDragEnd(
    params: GridCellParams,
    dragOffsetX: Float,
    dragOffsetY: Float,
    callbacks: CellDragCallbacks
) {
    val item = params.item
    val currentX = (item.col * params.cellWidthPx) + dragOffsetX
    val currentY = (item.row * params.cellHeightPx) + dragOffsetY
    val snap = GridEngineUtils.calculateSnapCell(
        params = SnapParams(
            xPx = currentX,
            yPx = currentY,
            cellWidthPx = params.cellWidthPx,
            cellHeightPx = params.cellHeightPx,
            colSpan = item.colSpan,
            rowSpan = item.rowSpan
        ),
        limits = params.limits
    )
    val validSlot = GridEngineUtils.findNearestValidSlot(
        targetCol = snap.first,
        targetRow = snap.second,
        item = item,
        items = params.items,
        limits = params.limits
    )
    if (validSlot != null) {
        callbacks.onReset()
        callbacks.onMove(item.id, validSlot.first, validSlot.second)
    } else {
        callbacks.onAnimate()
    }
}

@Composable
private fun BoxScope.RightEdgeResizeHandle(
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
                        val currW = (currentItem.colSpan * currentParams.cellWidthPx) + totalDragX
                        val targetColSpan = ceil((currW / currentParams.cellWidthPx) - RESIZE_SNAP_EPSILON).toInt()
                            .coerceIn(currentItem.minColSpan, currentParams.limits.maxCols - currentItem.col)
                        val resizedItem = currentItem.copy(colSpan = targetColSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, currentParams.items)
                        currentCallbacks.onResetExtra()
                        if (!hasCollision && targetColSpan != currentItem.colSpan) {
                            currentCallbacks.onResizeItem(currentItem.id, targetColSpan, currentItem.rowSpan)
                        }
                    },
                    onDragCancel = { currentCallbacks.onResizing(false); currentCallbacks.onResetExtra() },
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
private fun BoxScope.BottomEdgeResizeHandle(
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
                        val currH = (currentItem.rowSpan * currentParams.cellHeightPx) + totalDragY
                        val targetRowSpan = ceil((currH / currentParams.cellHeightPx) - RESIZE_SNAP_EPSILON).toInt()
                            .coerceIn(currentItem.minRowSpan, currentParams.limits.maxRows - currentItem.row)
                        val resizedItem = currentItem.copy(rowSpan = targetRowSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, currentParams.items)
                        currentCallbacks.onResetExtra()
                        if (!hasCollision && targetRowSpan != currentItem.rowSpan) {
                            currentCallbacks.onResizeItem(currentItem.id, currentItem.colSpan, targetRowSpan)
                        }
                    },
                    onDragCancel = { currentCallbacks.onResizing(false); currentCallbacks.onResetExtra() },
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
private fun BoxScope.CornerResizeHandle(
    params: GridCellParams,
    callbacks: CornerResizeCallbacks
) {
    val currentItem by rememberUpdatedState(params.item)
    val currentParams by rememberUpdatedState(params)
    val currentCallbacks by rememberUpdatedState(callbacks)

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(CORNER_TOUCH_SIZE)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = CORNER_HANDLE_RADIUS)
            )
            .pointerInput(params.item.id) { detectTapGestures(onTap = { currentCallbacks.onEditItem() }) }
            .pointerInput(params.item.id) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = { 
                        totalDragX = 0f
                        totalDragY = 0f
                        currentCallbacks.onResizing(true) 
                    },
                    onDragEnd = {
                        currentCallbacks.onResizing(false)
                        val currW = (currentItem.colSpan * currentParams.cellWidthPx) + totalDragX
                        val currH = (currentItem.rowSpan * currentParams.cellHeightPx) + totalDragY
                        val targetColSpan = ceil((currW / currentParams.cellWidthPx) - RESIZE_SNAP_EPSILON).toInt()
                            .coerceIn(currentItem.minColSpan, currentParams.limits.maxCols - currentItem.col)
                        val targetRowSpan = ceil((currH / currentParams.cellHeightPx) - RESIZE_SNAP_EPSILON).toInt()
                            .coerceIn(currentItem.minRowSpan, currentParams.limits.maxRows - currentItem.row)
                        val resizedItem = currentItem.copy(colSpan = targetColSpan, rowSpan = targetRowSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, currentParams.items)
                        currentCallbacks.onResetExtra()
                        val colSpanChanged = targetColSpan != currentItem.colSpan
                        val rowSpanChanged = targetRowSpan != currentItem.rowSpan
                        if (!hasCollision && (colSpanChanged || rowSpanChanged)) {
                            currentCallbacks.onResizeItem(currentItem.id, targetColSpan, targetRowSpan)
                        }
                    },
                    onDragCancel = { currentCallbacks.onResizing(false); currentCallbacks.onResetExtra() },
                    onDrag = { change, dragAmount -> 
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                        currentCallbacks.onDelta(dragAmount.x, dragAmount.y) 
                    }
                )
            },
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
