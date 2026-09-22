package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val HANDLE_TOUCH_SIZE = 28.dp
private val CORNER_TOUCH_SIZE = 36.dp

private data class GridMetricsTuple(
    val cellWidthPx: Float,
    val cellHeightPx: Float,
    val cellWidthDp: Dp,
    val cellHeightDp: Dp
)

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

data class DragTargetSlot(
    val itemId: String,
    val col: Int,
    val row: Int,
    val colSpan: Int,
    val rowSpan: Int,
    val isValid: Boolean
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
    val isDragging: Boolean,
    val isResizing: Boolean,
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
    var activeDragTarget by remember { mutableStateOf<DragTargetSlot?>(null) }

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
        val editTopPaddingDp = if (isEditMode) 52.dp else 0.dp

        val metrics = remember(
            constraints.maxWidth,
            constraints.maxHeight,
            editTopPaddingDp,
            limits
        ) {
            val wPx = with(density) { constraints.maxWidth.toDp().toPx() }
            val hPx = with(density) { (constraints.maxHeight.toDp() - editTopPaddingDp).toPx() }
            val cWidthPx = wPx / limits.maxCols
            val cHeightPx = hPx / limits.maxRows
            val cWidthDp = with(density) { cWidthPx.toDp() }
            val cHeightDp = with(density) { cHeightPx.toDp() }
            GridMetricsTuple(cWidthPx, cHeightPx, cWidthDp, cHeightDp)
        }
        val cellWidthPx = metrics.cellWidthPx
        val cellHeightPx = metrics.cellHeightPx
        val cellWidthDp = metrics.cellWidthDp
        val cellHeightDp = metrics.cellHeightDp

        if (isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = editTopPaddingDp)
            ) {
                GridBackgroundOverlay(
                    limits = limits,
                    cellWidthPx = cellWidthPx,
                    cellHeightPx = cellHeightPx,
                    activeDragTarget = activeDragTarget
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = editTopPaddingDp)
        ) {
            items.forEach { item ->
                key(item.id) {
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
                    GridItemCell(
                        params = params,
                        actions = actions,
                        onDragTargetChange = { activeDragTarget = it }
                    ) {
                        itemContent(item)
                    }
                }
            }
        }

        if (isEditMode) {
            GridTopEditBar(
                onSave = actions.onSaveGridEditMode,
                onCancel = actions.onCancelGridEditMode,
                onAddContainer = actions.onOpenAddContainerDialog,
                onSettings = actions.onSettingsClick
            )
        }
    }
}

@Composable
private fun GridTopEditBar(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onAddContainer: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .zIndex(100f),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onCancel,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancelar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Cancelar", maxLines = 1, style = MaterialTheme.typography.labelMedium)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onSettings,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações",
                        modifier = Modifier.size(20.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = onAddContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Container",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Salvar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Salvar",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GridBackgroundOverlay(
    limits: GridLimits,
    cellWidthPx: Float,
    cellHeightPx: Float,
    activeDragTarget: DragTargetSlot?,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

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
                    topLeft = Offset(
                        (col * cellWidthPx) + GRID_OFFSET_PX,
                        (row * cellHeightPx) + GRID_OFFSET_PX
                    ),
                    size = Size(cellWidthPx - GRID_MARGIN_PX, cellHeightPx - GRID_MARGIN_PX),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS),
                    style = stroke
                )
            }
        }

        activeDragTarget?.let { target ->
            val targetColor = if (target.isValid) primaryColor else errorColor
            val left = (target.col * cellWidthPx) + GRID_OFFSET_PX
            val top = (target.row * cellHeightPx) + GRID_OFFSET_PX
            val width = (target.colSpan * cellWidthPx) - GRID_MARGIN_PX
            val height = (target.rowSpan * cellHeightPx) - GRID_MARGIN_PX

            if (width > 0f && height > 0f) {
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.25f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS)
                )
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.8f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
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
    onDragTargetChange: (DragTargetSlot?) -> Unit,
    content: @Composable () -> Unit
) {
    val currentParams by rememberUpdatedState(params)
    val currentActions by rememberUpdatedState(actions)
    val currentOnDragTargetChange by rememberUpdatedState(onDragTargetChange)

    val item = params.item
    val coroutineScope = rememberCoroutineScope()

    var resizeExtraWidthPx by remember { mutableFloatStateOf(0f) }
    var resizeExtraHeightPx by remember { mutableFloatStateOf(0f) }
    var isResizing by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val animatableOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val density = LocalDensity.current
    val currentWidthDp =
        (params.cellWidthDp * item.colSpan) + with(density) { resizeExtraWidthPx.toDp() }
    val currentHeightDp =
        (params.cellHeightDp * item.rowSpan) + with(density) { resizeExtraHeightPx.toDp() }
    val leftDp = params.cellWidthDp * item.col
    val topDp = params.cellHeightDp * item.row

    val totalDragX = animatableOffset.value.x + dragOffsetX
    val totalDragY = animatableOffset.value.y + dragOffsetY

    val editModifier = Modifier.gridCellEditModifier(
        GridEditModifierParams(
            isEditMode = params.isEditMode,
            itemId = item.id,
            isDragging = isDragging,
            isResizing = isResizing,
            onDragStart = {
                isDragging = true
                dragOffsetX = 0f
                dragOffsetY = 0f
                coroutineScope.launch { animatableOffset.snapTo(Offset.Zero) }
            },
            onDragEnd = {
                val activeParams = currentParams
                val activeItem = activeParams.item
                val activeActions = currentActions

                val currentX = (activeItem.col * activeParams.cellWidthPx) + dragOffsetX
                val currentY = (activeItem.row * activeParams.cellHeightPx) + dragOffsetY

                val snap = GridEngineUtils.calculateSnapCell(
                    params = SnapParams(
                        xPx = currentX,
                        yPx = currentY,
                        cellWidthPx = activeParams.cellWidthPx,
                        cellHeightPx = activeParams.cellHeightPx,
                        colSpan = activeItem.colSpan,
                        rowSpan = activeItem.rowSpan
                    ),
                    limits = activeParams.limits
                )

                val validSlot = GridEngineUtils.findNearestValidSlot(
                    targetCol = snap.first,
                    targetRow = snap.second,
                    item = activeItem,
                    items = activeParams.items,
                    limits = activeParams.limits
                )

                isDragging = false
                currentOnDragTargetChange(null)

                if (validSlot != null && (validSlot.first != activeItem.col || validSlot.second != activeItem.row)) {
                    dragOffsetX = 0f
                    dragOffsetY = 0f
                    coroutineScope.launch { animatableOffset.snapTo(Offset.Zero) }
                    activeActions.onMoveGridItem(activeItem.id, validSlot.first, validSlot.second)
                } else {
                    val finalOffset = Offset(dragOffsetX, dragOffsetY)
                    dragOffsetX = 0f
                    dragOffsetY = 0f
                    coroutineScope.launch {
                        animatableOffset.snapTo(finalOffset)
                        animatableOffset.animateTo(
                            targetValue = Offset.Zero,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                }
            },
            onDragCancel = {
                val finalOffset = Offset(dragOffsetX, dragOffsetY)
                isDragging = false
                dragOffsetX = 0f
                dragOffsetY = 0f
                currentOnDragTargetChange(null)
                coroutineScope.launch {
                    animatableOffset.snapTo(finalOffset)
                    animatableOffset.animateTo(
                        targetValue = Offset.Zero,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            },
            onDrag = { _, dragAmount ->
                dragOffsetX += dragAmount.x
                dragOffsetY += dragAmount.y

                val activeParams = currentParams
                val activeItem = activeParams.item
                val currentX = (activeItem.col * activeParams.cellWidthPx) + dragOffsetX
                val currentY = (activeItem.row * activeParams.cellHeightPx) + dragOffsetY

                val snap = GridEngineUtils.calculateSnapCell(
                    params = SnapParams(
                        xPx = currentX,
                        yPx = currentY,
                        cellWidthPx = activeParams.cellWidthPx,
                        cellHeightPx = activeParams.cellHeightPx,
                        colSpan = activeItem.colSpan,
                        rowSpan = activeItem.rowSpan
                    ),
                    limits = activeParams.limits
                )

                val validSlot = GridEngineUtils.findNearestValidSlot(
                    targetCol = snap.first,
                    targetRow = snap.second,
                    item = activeItem,
                    items = activeParams.items,
                    limits = activeParams.limits
                )

                if (validSlot != null) {
                    currentOnDragTargetChange(
                        DragTargetSlot(
                            itemId = activeItem.id,
                            col = validSlot.first,
                            row = validSlot.second,
                            colSpan = activeItem.colSpan,
                            rowSpan = activeItem.rowSpan,
                            isValid = true
                        )
                    )
                } else {
                    currentOnDragTargetChange(
                        DragTargetSlot(
                            itemId = activeItem.id,
                            col = snap.first,
                            row = snap.second,
                            colSpan = activeItem.colSpan,
                            rowSpan = activeItem.rowSpan,
                            isValid = false
                        )
                    )
                }
            }
        )
    )

    GridItemCellBox(
        CellBoxConfig(
            metrics = CellBoxMetrics(
                leftDp,
                topDp,
                currentWidthDp,
                currentHeightDp,
                totalDragX,
                totalDragY
            ),
            modifier = editModifier,
            params = params,
            isDragging = isDragging,
            isResizing = isResizing,
            callbacks = CellBoxCallbacks(
                onResizingChange = { isResizing = it },
                onResizeWidthChange = { resizeExtraWidthPx += it },
                onResizeHeightChange = { resizeExtraHeightPx += it },
                onResizeDelta = { dx, dy ->
                    resizeExtraWidthPx += dx
                    resizeExtraHeightPx += dy
                },
                onResetWidth = { resizeExtraWidthPx = 0f },
                onResetHeight = { resizeExtraHeightPx = 0f },
                onResetAll = {
                    resizeExtraWidthPx = 0f
                    resizeExtraHeightPx = 0f
                }
            ),
            actions = actions,
            content = content
        )
    )
}

@Composable
private fun GridItemCellBox(config: CellBoxConfig) {
    val metrics = config.metrics
    val params = config.params
    val item = params.item
    val isLifted = params.isEditMode && (config.isDragging || config.isResizing)

    Box(
        modifier = Modifier
            .offset(x = metrics.leftDp, y = metrics.topDp)
            .size(width = metrics.currentWidthDp, height = metrics.currentHeightDp)
            .zIndex(if (isLifted) 10f else 1f)
            .graphicsLayer {
                translationX = metrics.totalDragX
                translationY = metrics.totalDragY
                scaleX = if (isLifted) 1.02f else 1f
                scaleY = if (isLifted) 1.02f else 1f
            }
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
                shape = RoundedCornerShape(BORDER_CORNER_RADIUS)
            )
            .clip(RoundedCornerShape(BORDER_CORNER_RADIUS))
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

@Composable
private fun GridItemEditOverlay(
    params: GridCellParams,
    callbacks: GridItemEditCallbacks
) {
    val item = params.item
    val blockerModifier = if (item.safeType == LauncherItemType.SCROLL_VIEW) {
        Modifier
    } else {
        Modifier.pointerInput(item.id + "_click_blocker") {
            detectTapGestures(onTap = {})
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(blockerModifier)
    ) {
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
                        val item = currentItem
                        val p = currentParams
                        val currW = (item.colSpan * p.cellWidthPx) + totalDragX
                        val targetColSpan = (currW / p.cellWidthPx).roundToInt()
                            .coerceIn(item.minColSpan, p.limits.maxCols - item.col)
                        val resizedItem = item.copy(colSpan = targetColSpan)
                        val hasCollision =
                            GridEngineUtils.checkCollisionWithOthers(resizedItem, p.items)
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
                        val item = currentItem
                        val p = currentParams
                        val currH = (item.rowSpan * p.cellHeightPx) + totalDragY
                        val targetRowSpan = (currH / p.cellHeightPx).roundToInt()
                            .coerceIn(item.minRowSpan, p.limits.maxRows - item.row)
                        val resizedItem = item.copy(rowSpan = targetRowSpan)
                        val hasCollision =
                            GridEngineUtils.checkCollisionWithOthers(resizedItem, p.items)
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
                        val item = currentItem
                        val p = currentParams
                        val currW = (item.colSpan * p.cellWidthPx) + totalDragX
                        val currH = (item.rowSpan * p.cellHeightPx) + totalDragY
                        val targetColSpan = (currW / p.cellWidthPx).roundToInt()
                            .coerceIn(item.minColSpan, p.limits.maxCols - item.col)
                        val targetRowSpan = (currH / p.cellHeightPx).roundToInt()
                            .coerceIn(item.minRowSpan, p.limits.maxRows - item.row)
                        val resizedItem =
                            item.copy(colSpan = targetColSpan, rowSpan = targetRowSpan)
                        val hasCollision =
                            GridEngineUtils.checkCollisionWithOthers(resizedItem, p.items)
                        currentCallbacks.onResetExtra()
                        val colSpanChanged = targetColSpan != item.colSpan
                        val rowSpanChanged = targetRowSpan != item.rowSpan
                        if (!hasCollision && (colSpanChanged || rowSpanChanged)) {
                            currentCallbacks.onResizeItem(item.id, targetColSpan, targetRowSpan)
                        }
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
