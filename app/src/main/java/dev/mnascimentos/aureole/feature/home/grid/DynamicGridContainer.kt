package dev.mnascimentos.aureole.feature.home.grid

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemEditCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.GridMetricsTuple
import dev.mnascimentos.aureole.feature.home.grid.model.ResizeHandleCallbacks
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

internal val BORDER_CORNER_RADIUS = 16.dp
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

    BackHandler(enabled = isEditMode) {
        actions.onSaveGridEditMode()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isEditMode) {
                detectTapGestures(
                    onTap = {
                        if (isEditMode) {
                            actions.onSaveGridEditMode()
                        }
                    },
                    onLongPress = { if (!isEditMode) actions.onEnterGridEditMode() }
                )
            }
    ) {
        val density = LocalDensity.current

        val metrics = remember(constraints.maxWidth, constraints.maxHeight, limits) {
            val wPx = with(density) { constraints.maxWidth.toDp().toPx() }
            val hPx = with(density) { constraints.maxHeight.toDp().toPx() }
            val cWidthPx = wPx / limits.maxCols
            val cHeightPx = hPx / limits.maxRows
            GridMetricsTuple(cWidthPx, cHeightPx, with(density) { cWidthPx.toDp() }, with(density) { cHeightPx.toDp() })
        }

        if (isEditMode) {
            Box(modifier = Modifier.fillMaxSize()) {
                GridBackgroundOverlay(
                    limits = limits,
                    cellWidthPx = metrics.cellWidthPx,
                    cellHeightPx = metrics.cellHeightPx,
                    activeDragTarget = activeDragTarget
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
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
            GridBottomEditControls(
                onAddContainer = actions.onOpenAddContainerDialog,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(24.dp)
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
                    onEditItem = { config.actions.onOpenEditGridItemDialog(item) }
                )
            )
        }
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
