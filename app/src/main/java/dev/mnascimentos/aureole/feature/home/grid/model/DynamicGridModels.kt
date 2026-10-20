package dev.mnascimentos.aureole.feature.home.grid.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import kotlinx.coroutines.CoroutineScope

data class GridLimits(
    val maxCols: Int = 4,
    val maxRows: Int = 8
)

data class SnapParams(
    val xPx: Float,
    val yPx: Float,
    val cellWidthPx: Float,
    val cellHeightPx: Float,
    val colSpan: Int,
    val rowSpan: Int
)

data class SlotSearchRequest(
    val targetColSpan: Int,
    val targetRowSpan: Int,
    val minColSpan: Int = 1,
    val minRowSpan: Int = 1,
    val limits: GridLimits = GridLimits()
)

data class SlotSearchParams(
    val targetCol: Int,
    val targetRow: Int,
    val item: LauncherItemState,
    val items: List<LauncherItemState>,
    val limits: GridLimits,
    val maxCol: Int,
    val maxRow: Int
)

data class DynamicGridItemsParams(
    val items: List<LauncherItemState>,
    val isEditMode: Boolean,
    val metrics: GridMetricsTuple,
    val limits: GridLimits,
    val actions: HomeScreenActions
)

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

data class ScrollViewChildParams(
    val parentId: String,
    val child: LauncherItemState,
    val childIndex: Int,
    val totalChildren: Int,
    val isVertical: Boolean,
)

data class GridItemSpec(
    val type: LauncherItemType,
    val widgetId: Int? = null,
    val targetColSpan: Int? = null,
    val targetRowSpan: Int? = null,
    val minColSpan: Int? = null,
    val minRowSpan: Int? = null,
    val scrollOrientation: ScrollOrientation? = null
)

data class GridMetricsTuple(
    val cellWidthPx: Float,
    val cellHeightPx: Float,
    val cellWidthDp: Dp,
    val cellHeightDp: Dp
)

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

data class ResizeHandleCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onDelta: (Float) -> Unit,
    val onResetExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit
)

data class CornerResizeCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onDelta: (Float, Float) -> Unit,
    val onResetExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit,
    val onEditItem: () -> Unit
)

data class GridItemEditCallbacks(
    val onResizing: (Boolean) -> Unit,
    val onResizeWidthDelta: (Float) -> Unit,
    val onResizeHeightDelta: (Float) -> Unit,
    val onResizeDelta: (Float, Float) -> Unit,
    val onResetWidthExtra: () -> Unit,
    val onResetHeightExtra: () -> Unit,
    val onResetAllExtra: () -> Unit,
    val onResizeItem: (String, Int, Int) -> Unit,
    val onEditItem: () -> Unit,
    val onDeleteItem: () -> Unit
)

data class GridEditModifierParams(
    val isEditMode: Boolean,
    val itemId: String,
    val isDragging: Boolean,
    val isResizing: Boolean,
    val onDragStart: () -> Unit,
    val onDragEnd: () -> Unit,
    val onDragCancel: () -> Unit,
    val onDrag: (PointerInputChange, Offset) -> Unit
)

data class CellBoxCallbacks(
    val onResizingChange: (Boolean) -> Unit,
    val onResizeWidthChange: (Float) -> Unit,
    val onResizeHeightChange: (Float) -> Unit,
    val onResizeDelta: (Float, Float) -> Unit,
    val onResetWidth: () -> Unit,
    val onResetHeight: () -> Unit,
    val onResetAll: () -> Unit
)

data class CellBoxMetrics(
    val leftDp: Dp,
    val topDp: Dp,
    val currentWidthDp: Dp,
    val currentHeightDp: Dp,
    val totalDragX: Float,
    val totalDragY: Float
)

data class CellBoxConfig(
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
