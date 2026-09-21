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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.math.roundToInt

private val HANDLE_TOUCH_SIZE = 28.dp
private val CORNER_TOUCH_SIZE = 36.dp
private val BORDER_CORNER_RADIUS = 16.dp
private val CORNER_HANDLE_RADIUS = 12.dp
private const val GRID_OFFSET_PX = 8f
private const val GRID_MARGIN_PX = 16f
private const val GRID_LINE_WIDTH = 2f
private const val GRID_CORNER_RADIUS = 24f
private const val DASH_LENGTH_PX = 10f

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
    val resizeExtraWidthPx: Float,
    val resizeExtraHeightPx: Float,
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
                        if (!isEditMode) actions.onToggleGridEditMode()
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
            GridTopEditBar(onSave = actions.onToggleGridEditMode)
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
private fun GridTopEditBar(onSave: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Modo de Edição", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Salvar")
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
            resizeExtraWidthPx = resizeExtraWidthPx,
            resizeExtraHeightPx = resizeExtraHeightPx,
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
                resizeExtraWidthPx = config.resizeExtraWidthPx,
                resizeExtraHeightPx = config.resizeExtraHeightPx,
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
    resizeExtraWidthPx: Float,
    resizeExtraHeightPx: Float,
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
        resizeExtraWidthPx = resizeExtraWidthPx,
        callbacks = ResizeHandleCallbacks(
            onResizing = callbacks.onResizing,
            onDelta = callbacks.onResizeWidthDelta,
            onResetExtra = callbacks.onResetWidthExtra,
            onResizeItem = callbacks.onResizeItem
        )
    )

    BottomEdgeResizeHandle(
        params = params,
        resizeExtraHeightPx = resizeExtraHeightPx,
        callbacks = ResizeHandleCallbacks(
            onResizing = callbacks.onResizing,
            onDelta = callbacks.onResizeHeightDelta,
            onResetExtra = callbacks.onResetHeightExtra,
            onResizeItem = callbacks.onResizeItem
        )
    )

    CornerResizeHandle(
        params = params,
        resizeExtraWidthPx = resizeExtraWidthPx,
        resizeExtraHeightPx = resizeExtraHeightPx,
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
    resizeExtraWidthPx: Float,
    callbacks: ResizeHandleCallbacks
) {
    val item = params.item
    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .width(HANDLE_TOUCH_SIZE)
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = { callbacks.onResizing(true) },
                    onDragEnd = {
                        callbacks.onResizing(false)
                        val currW = (item.colSpan * params.cellWidthPx) + resizeExtraWidthPx
                        val targetColSpan = (currW / params.cellWidthPx).roundToInt()
                            .coerceIn(1, params.limits.maxCols - item.col)
                        val resizedItem = item.copy(colSpan = targetColSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, params.items)
                        callbacks.onResetExtra()
                        if (!hasCollision) callbacks.onResizeItem(item.id, targetColSpan, item.rowSpan)
                    },
                    onDragCancel = { callbacks.onResizing(false); callbacks.onResetExtra() },
                    onDrag = { change, dragAmount -> change.consume(); callbacks.onDelta(dragAmount.x) }
                )
            }
    )
}

@Composable
private fun BoxScope.BottomEdgeResizeHandle(
    params: GridCellParams,
    resizeExtraHeightPx: Float,
    callbacks: ResizeHandleCallbacks
) {
    val item = params.item
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(HANDLE_TOUCH_SIZE)
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = { callbacks.onResizing(true) },
                    onDragEnd = {
                        callbacks.onResizing(false)
                        val currH = (item.rowSpan * params.cellHeightPx) + resizeExtraHeightPx
                        val targetRowSpan = (currH / params.cellHeightPx).roundToInt()
                            .coerceIn(1, params.limits.maxRows - item.row)
                        val resizedItem = item.copy(rowSpan = targetRowSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, params.items)
                        callbacks.onResetExtra()
                        if (!hasCollision) callbacks.onResizeItem(item.id, item.colSpan, targetRowSpan)
                    },
                    onDragCancel = { callbacks.onResizing(false); callbacks.onResetExtra() },
                    onDrag = { change, dragAmount -> change.consume(); callbacks.onDelta(dragAmount.y) }
                )
            }
    )
}

@Composable
private fun BoxScope.CornerResizeHandle(
    params: GridCellParams,
    resizeExtraWidthPx: Float,
    resizeExtraHeightPx: Float,
    callbacks: CornerResizeCallbacks
) {
    val item = params.item
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(CORNER_TOUCH_SIZE)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = CORNER_HANDLE_RADIUS)
            )
            .pointerInput(item.id) { detectTapGestures(onTap = { callbacks.onEditItem() }) }
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = { callbacks.onResizing(true) },
                    onDragEnd = {
                        callbacks.onResizing(false)
                        val currW = (item.colSpan * params.cellWidthPx) + resizeExtraWidthPx
                        val currH = (item.rowSpan * params.cellHeightPx) + resizeExtraHeightPx
                        val targetColSpan = (currW / params.cellWidthPx).roundToInt()
                            .coerceIn(1, params.limits.maxCols - item.col)
                        val targetRowSpan = (currH / params.cellHeightPx).roundToInt()
                            .coerceIn(1, params.limits.maxRows - item.row)
                        val resizedItem = item.copy(colSpan = targetColSpan, rowSpan = targetRowSpan)
                        val hasCollision = GridEngineUtils.checkCollisionWithOthers(resizedItem, params.items)
                        callbacks.onResetExtra()
                        if (!hasCollision) callbacks.onResizeItem(item.id, targetColSpan, targetRowSpan)
                    },
                    onDragCancel = { callbacks.onResizing(false); callbacks.onResetExtra() },
                    onDrag = { change, dragAmount -> change.consume(); callbacks.onDelta(dragAmount.x, dragAmount.y) }
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
