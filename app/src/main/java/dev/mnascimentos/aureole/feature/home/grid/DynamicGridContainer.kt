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
import androidx.compose.runtime.mutableFloatStateOf
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

    val currentWidthDp = (params.cellWidthDp * item.colSpan) + with(density) { resizeExtraWidthPx.toDp() }
    val currentHeightDp = (params.cellHeightDp * item.rowSpan) + with(density) { resizeExtraHeightPx.toDp() }

    val editParams = buildGridEditModifierParams(
        isEditMode = params.isEditMode,
        itemId = item.id,
        isDragging = isDragging,
        isResizing = isResizing,
        onStartDrag = {
            isDragging = true
            dragOffsetX = 0f
            dragOffsetY = 0f
            coroutineScope.launch { animatableOffset.snapTo(Offset.Zero) }
        },
        onEndDrag = {
            handleDragEnd(
                dragParams = DragEndParams(
                    params = currentParams,
                    dragOffsetX = dragOffsetX,
                    dragOffsetY = dragOffsetY,
                    coroutineScope = coroutineScope,
                    animatableOffset = animatableOffset
                ),
                onMoveItem = { id, c, r -> currentActions.onMoveGridItem(id, c, r) },
                onFinishDrag = {
                    isDragging = false
                    dragOffsetX = 0f
                    dragOffsetY = 0f
                    currentOnDragTargetChange(null)
                }
            )
        },
        onCancelDrag = {
            isDragging = false
            dragOffsetX = 0f
            dragOffsetY = 0f
            currentOnDragTargetChange(null)
        },
        onDeltaDrag = { dx, dy ->
            dragOffsetX += dx
            dragOffsetY += dy
            updateDragTargetSlot(currentParams, dragOffsetX, dragOffsetY, currentOnDragTargetChange)
        }
    )

    val editModifier = Modifier.gridCellEditModifier(editParams)

    val boxMetrics = CellBoxMetrics(
        leftDp = params.cellWidthDp * item.col,
        topDp = params.cellHeightDp * item.row,
        currentWidthDp = currentWidthDp,
        currentHeightDp = currentHeightDp,
        totalDragX = animatableOffset.value.x + dragOffsetX,
        totalDragY = animatableOffset.value.y + dragOffsetY
    )

    val boxCallbacks = CellBoxCallbacks(
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

private fun buildGridEditModifierParams(
    isEditMode: Boolean,
    itemId: String,
    isDragging: Boolean,
    isResizing: Boolean,
    onStartDrag: () -> Unit,
    onEndDrag: () -> Unit,
    onCancelDrag: () -> Unit,
    onDeltaDrag: (Float, Float) -> Unit
): GridEditModifierParams {
    return GridEditModifierParams(
        isEditMode = isEditMode,
        itemId = itemId,
        isDragging = isDragging,
        isResizing = isResizing,
        onDragStart = onStartDrag,
        onDragEnd = onEndDrag,
        onDragCancel = onCancelDrag,
        onDrag = { _, amount -> onDeltaDrag(amount.x, amount.y) }
    )
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
