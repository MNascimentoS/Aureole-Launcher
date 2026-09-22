package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.CellBoxCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.CellBoxConfig
import dev.mnascimentos.aureole.feature.home.grid.model.CellBoxMetrics
import dev.mnascimentos.aureole.feature.home.grid.model.CornerResizeCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditModifierParams
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemEditCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.GridMetricsTuple
import dev.mnascimentos.aureole.feature.home.grid.model.ResizeHandleCallbacks
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val BORDER_CORNER_RADIUS = 16.dp
private const val LIFTED_Z_INDEX = 10f
private const val LIFTED_SCALE = 1.02f

data class DynamicGridItemsParams(
    val items: List<LauncherItemState>,
    val isEditMode: Boolean,
    val metrics: GridMetricsTuple,
    val limits: GridLimits,
    val actions: HomeScreenActions
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
                    onLongPress = { if (!isEditMode) actions.onEnterGridEditMode() }
                )
            }
    ) {
        val density = LocalDensity.current
        val editTopPaddingDp = if (isEditMode) 52.dp else 0.dp

        val metrics = remember(constraints.maxWidth, constraints.maxHeight, editTopPaddingDp, limits) {
            val wPx = with(density) { constraints.maxWidth.toDp().toPx() }
            val hPx = with(density) { (constraints.maxHeight.toDp() - editTopPaddingDp).toPx() }
            val cWidthPx = wPx / limits.maxCols
            val cHeightPx = hPx / limits.maxRows
            GridMetricsTuple(cWidthPx, cHeightPx, with(density) { cWidthPx.toDp() }, with(density) { cHeightPx.toDp() })
        }

        if (isEditMode) {
            Box(modifier = Modifier.fillMaxSize().offset(y = editTopPaddingDp)) {
                GridBackgroundOverlay(
                    limits = limits,
                    cellWidthPx = metrics.cellWidthPx,
                    cellHeightPx = metrics.cellHeightPx,
                    activeDragTarget = activeDragTarget
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().offset(y = editTopPaddingDp)) {
            DynamicGridItemsList(
                params = DynamicGridItemsParams(
                    items = items,
                    isEditMode = isEditMode,
                    metrics = metrics,
                    limits = limits,
                    actions = actions
                ),
                onDragTargetChange = { activeDragTarget = it },
                itemContent = itemContent
            )
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
private fun DynamicGridItemsList(
    params: DynamicGridItemsParams,
    onDragTargetChange: (DragTargetSlot?) -> Unit,
    itemContent: @Composable (item: LauncherItemState) -> Unit
) {
    params.items.forEach { item ->
        key(item.id) {
            val cellParams = GridCellParams(
                item = item,
                items = params.items,
                isEditMode = params.isEditMode,
                cellWidthPx = params.metrics.cellWidthPx,
                cellHeightPx = params.metrics.cellHeightPx,
                cellWidthDp = params.metrics.cellWidthDp,
                cellHeightDp = params.metrics.cellHeightDp,
                limits = params.limits
            )
            GridItemCell(
                params = cellParams,
                actions = params.actions,
                onDragTargetChange = onDragTargetChange
            ) {
                itemContent(item)
            }
        }
    }
}

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
    var resizeExtra by remember { mutableStateOf(Offset.Zero) }
    var isResizing by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val animOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val wDp = (params.cellWidthDp * item.colSpan) + with(LocalDensity.current) { resizeExtra.x.toDp() }
    val hDp = (params.cellHeightDp * item.rowSpan) + with(LocalDensity.current) { resizeExtra.y.toDp() }
    val editModifier = rememberGridCellEditModifier(
        GridEditModifierArgs(
            params = params,
            isDragging = isDragging,
            isResizing = isResizing,
            onDraggingChange = { isDragging = it },
            dragOffset = dragOffset,
            onDragOffsetChange = { dragOffset = it },
            animatableOffset = animOffset,
            coroutineScope = coroutineScope,
            currentParams = currentParams,
            currentActions = currentActions,
            currentOnDragTargetChange = currentOnDragTargetChange
        )
    )
    val boxMetrics = CellBoxMetrics(
        leftDp = params.cellWidthDp * item.col,
        topDp = params.cellHeightDp * item.row,
        currentWidthDp = wDp,
        currentHeightDp = hDp,
        totalDragX = animOffset.value.x + dragOffset.x,
        totalDragY = animOffset.value.y + dragOffset.y
    )
    val boxCallbacks = rememberCellCallbacks(
        onResizingChange = { isResizing = it },
        onResizeWidthChange = { resizeExtra = resizeExtra.copy(x = resizeExtra.x + it) },
        onResizeHeightChange = { resizeExtra = resizeExtra.copy(y = resizeExtra.y + it) },
        onResetWidth = { resizeExtra = resizeExtra.copy(x = 0f) },
        onResetHeight = { resizeExtra = resizeExtra.copy(y = 0f) }
    )
    GridItemCellBox(
        CellBoxConfig(
            metrics = boxMetrics,
            modifier = editModifier,
            params = params,
            isDragging = isDragging,
            isResizing = isResizing,
            callbacks = boxCallbacks,
            actions = actions,
            content = content
        )
    )
}

private fun rememberCellCallbacks(
    onResizingChange: (Boolean) -> Unit,
    onResizeWidthChange: (Float) -> Unit,
    onResizeHeightChange: (Float) -> Unit,
    onResetWidth: () -> Unit,
    onResetHeight: () -> Unit
): CellBoxCallbacks {
    return CellBoxCallbacks(
        onResizingChange = onResizingChange,
        onResizeWidthChange = onResizeWidthChange,
        onResizeHeightChange = onResizeHeightChange,
        onResizeDelta = { dx, dy ->
            onResizeWidthChange(dx)
            onResizeHeightChange(dy)
        },
        onResetWidth = onResetWidth,
        onResetHeight = onResetHeight,
        onResetAll = {
            onResetWidth()
            onResetHeight()
        }
    )
}

@Composable
private fun rememberGridCellEditModifier(args: GridEditModifierArgs): Modifier {
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
                dragParams = DragEndParams(
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

data class DragEndParams(
    val params: GridCellParams,
    val dragOffsetX: Float,
    val dragOffsetY: Float,
    val coroutineScope: CoroutineScope,
    val animatableOffset: Animatable<Offset, AnimationVector2D>
)

private fun handleDragEnd(
    dragParams: DragEndParams,
    onMoveItem: (String, Int, Int) -> Unit,
    onFinishDrag: () -> Unit
) {
    val params = dragParams.params
    val dragOffsetX = dragParams.dragOffsetX
    val dragOffsetY = dragParams.dragOffsetY
    val coroutineScope = dragParams.coroutineScope
    val animatableOffset = dragParams.animatableOffset

    val activeItem = params.item
    val currentX = (activeItem.col * params.cellWidthPx) + dragOffsetX
    val currentY = (activeItem.row * params.cellHeightPx) + dragOffsetY

    val snap = GridEngineUtils.calculateSnapCell(
        params = SnapParams(
            currentX,
            currentY,
            params.cellWidthPx,
            params.cellHeightPx,
            activeItem.colSpan,
            activeItem.rowSpan
        ),
        limits = params.limits
    )

    val validSlot = GridEngineUtils.findNearestValidSlot(
        targetCol = snap.first,
        targetRow = snap.second,
        item = activeItem,
        items = params.items,
        limits = params.limits
    )

    onFinishDrag()

    if (validSlot != null && (validSlot.first != activeItem.col || validSlot.second != activeItem.row)) {
        coroutineScope.launch { animatableOffset.snapTo(Offset.Zero) }
        onMoveItem(activeItem.id, validSlot.first, validSlot.second)
    } else {
        val finalOffset = Offset(dragOffsetX, dragOffsetY)
        coroutineScope.launch {
            animatableOffset.snapTo(finalOffset)
            animatableOffset.animateTo(
                targetValue = Offset.Zero,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
    }
}

private fun updateDragTargetSlot(
    params: GridCellParams,
    dragOffsetX: Float,
    dragOffsetY: Float,
    onDragTargetChange: (DragTargetSlot?) -> Unit
) {
    val activeItem = params.item
    val currentX = (activeItem.col * params.cellWidthPx) + dragOffsetX
    val currentY = (activeItem.row * params.cellHeightPx) + dragOffsetY

    val snap = GridEngineUtils.calculateSnapCell(
        params = SnapParams(
            currentX,
            currentY,
            params.cellWidthPx,
            params.cellHeightPx,
            activeItem.colSpan,
            activeItem.rowSpan
        ),
        limits = params.limits
    )

    val validSlot = GridEngineUtils.findNearestValidSlot(
        targetCol = snap.first,
        targetRow = snap.second,
        item = activeItem,
        items = params.items,
        limits = params.limits
    )

    onDragTargetChange(
        DragTargetSlot(
            itemId = activeItem.id,
            col = validSlot?.first ?: snap.first,
            row = validSlot?.second ?: snap.second,
            colSpan = activeItem.colSpan,
            rowSpan = activeItem.rowSpan,
            isValid = validSlot != null
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
            .zIndex(if (isLifted) LIFTED_Z_INDEX else 1f)
            .graphicsLayer {
                translationX = metrics.totalDragX
                translationY = metrics.totalDragY
                scaleX = if (isLifted) LIFTED_SCALE else 1f
                scaleY = if (isLifted) LIFTED_SCALE else 1f
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
        Modifier.pointerInput(item.id + "_click_blocker") { detectTapGestures(onTap = {}) }
    }
    Box(
        modifier = Modifier.fillMaxSize().then(blockerModifier)
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
