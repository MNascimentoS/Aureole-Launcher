package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.reorder.model.LocalReorderHandle
import dev.mnascimentos.aureole.composable.reorder.model.ReorderDragState
import dev.mnascimentos.aureole.composable.reorder.model.ReorderHandle
import dev.mnascimentos.aureole.composable.reorder.model.ReorderMove
import dev.mnascimentos.aureole.composable.reorder.model.ReorderOrientation
import dev.mnascimentos.aureole.composable.reorder.model.ReorderSlotParams
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.reorderAxisDelta
import dev.mnascimentos.aureole.composable.reorder.model.reorderStepTarget
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import kotlinx.coroutines.launch

/** Scale of the item while the finger holds it. */
private const val DRAGGED_SCALE = 1.04f

/** Shadow of the held item, in px of elevation. */
private const val DRAGGED_ELEVATION = 16f

/** Draws the held item above its neighbours. */
private const val DRAGGED_Z_INDEX = 1f

/**
 * One item of [ReorderableRow] or [ReorderableColumn]: long-press then drag (the item, or only its handle when
 * [ReorderSlotParams.handleOnly]) to move it, a tick at every swap,
 * neighbours sliding to their new place, the optional ✕ badge in the top end corner and the accessibility actions
 * "move" and "remove".
 */
@Composable
internal fun ReorderableSlot(
    slot: ReorderSlotParams,
    state: ReorderDragState,
    actions: ReorderableActions,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val isActive = state.activeKey == slot.key
    val scale by animateFloatAsState(if (isActive && state.isDragging) DRAGGED_SCALE else 1f, label = "reorderScale")
    val onRemove = actions.onRemove
    val gestures = rememberReorderGestures(slot, state, actions, isRtl)
    Box(
        modifier = modifier
            .zIndex(if (isActive) DRAGGED_Z_INDEX else 0f)
            .then(rememberPlacementAnimation(isActive))
            .onSizeChanged { size ->
                val main = if (slot.orientation == ReorderOrientation.Horizontal) size.width else size.height
                state.onMeasured(slot.key, main.toFloat())
            }
            .graphicsLayer {
                val shift = if (isActive) state.offset else 0f
                if (slot.orientation == ReorderOrientation.Horizontal) {
                    translationX = if (isRtl) -shift else shift
                } else {
                    translationY = shift
                }
                scaleX = scale
                scaleY = scale
                shadowElevation = if (isActive) DRAGGED_ELEVATION * (scale - 1f) / (DRAGGED_SCALE - 1f) else 0f
            }
            .then(if (slot.handleOnly) Modifier else gestures)
            .then(reorderSemantics(slot, actions, isRtl))
    ) {
        if (slot.handleOnly) {
            val handle = ReorderHandle(gestures, isActive && state.isDragging)
            CompositionLocalProvider(LocalReorderHandle provides handle) { content() }
        } else {
            content()
        }
        if (onRemove != null && !isActive) {
            val overhang = reorderBadgeOverhang()
            RemoveBadge(
                onClick = { onRemove(slot.index) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = overhang, y = -overhang)
            )
        }
    }
}

/**
 * How far the ✕ badge sticks out past the top and end edges of its item. The item keeps its full size, so rows with
 * a badge line up with the rest of the sheet; [ReorderableRow] and [ReorderableColumn] leave this much room around
 * the list so the badge is never clipped by their scroll.
 */
@Composable
@ReadOnlyComposable
internal fun reorderBadgeOverhang(): Dp = AureoleDS.dimens.xSmall

@Composable
private fun RemoveBadge(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.core_reorder_remove)
    Box(
        modifier = modifier
            .size(AureoleDS.dimens.large)
            .clip(CircleShape)
            .background(AureoleDS.colors.surfaceVariant)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = label
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Close(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconXSmall)
        )
    }
}

/**
 * Long-press then drag: picks the item, reports each swap with a tick and settles it back when the finger lifts.
 * With [ReorderSlotParams.autoScroll], holding the item near an edge of the viewport scrolls the list and the
 * item stays under the finger.
 */
@Composable
private fun rememberReorderGestures(
    slot: ReorderSlotParams,
    state: ReorderDragState,
    actions: ReorderableActions,
    isRtl: Boolean,
): Modifier {
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val currentSlot by rememberUpdatedState(slot)
    val currentActions by rememberUpdatedState(actions)
    val source = remember { mutableStateOf<LayoutCoordinates?>(null) }
    val report = { swap: ReorderMove? ->
        if (swap != null) {
            currentActions.onMove(swap.from, swap.to)
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }
    val follow = { finger: Offset ->
        val coordinates = source.value
        val auto = currentSlot.autoScroll
        if (auto != null && coordinates != null && state.isDragging) {
            auto.track(coordinates, finger, currentSlot.orientation, isRtl)
            auto.scrollIfNearEdge(scope, density) { scrolled ->
                report(state.onScrolled(scrolled, currentSlot.spacingPx))
            }
        }
    }
    val finish = {
        currentSlot.autoScroll?.release()
        val key = currentSlot.key
        if (state.isDragging && state.activeKey == key) {
            if (state.release()) currentActions.onMoveEnd()
            scope.launch {
                animate(state.offset, 0f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { value, _ ->
                    state.settle(key, value)
                }
                state.clear(key)
            }
        }
    }
    return Modifier.onPlaced { source.value = it }.pointerInput(slot.key, isRtl) {
        detectDragGesturesAfterLongPress(
            onDragStart = { finger ->
                if (state.start(currentSlot.key, currentSlot.keys)) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    follow(finger)
                }
            },
            onDrag = { change, amount ->
                change.consume()
                report(state.drag(reorderAxisDelta(amount, currentSlot.orientation, isRtl), currentSlot.spacingPx))
                follow(change.position)
            },
            onDragEnd = { finish() },
            onDragCancel = { finish() }
        )
    }
}

/** "Move up/down" (columns) or "Move left/right" (rows) towards each neighbour that exists, and "Remove". */
@Composable
@ReadOnlyComposable
private fun reorderSemantics(
    slot: ReorderSlotParams,
    actions: ReorderableActions,
    isRtl: Boolean,
): Modifier {
    val isRow = slot.orientation == ReorderOrientation.Horizontal
    val startLabel = stringResource(if (isRow) R.string.core_reorder_move_left else R.string.core_reorder_move_up)
    val endLabel = stringResource(if (isRow) R.string.core_reorder_move_right else R.string.core_reorder_move_down)
    val removeLabel = stringResource(R.string.core_reorder_remove)
    // In a right-to-left row, "left" points to the end of the list.
    val leftIsStart = !(isRow && isRtl)
    val moveAction = { label: String, towardsStart: Boolean ->
        reorderStepTarget(slot.index, slot.keys.size, towardsStart)?.let { target ->
            CustomAccessibilityAction(label) {
                actions.onMove(slot.index, target)
                actions.onMoveEnd()
                true
            }
        }
    }
    val list = listOfNotNull(
        moveAction(startLabel, leftIsStart),
        moveAction(endLabel, !leftIsStart),
        actions.onRemove?.let { remove ->
            CustomAccessibilityAction(removeLabel) {
                remove(slot.index)
                true
            }
        }
    )
    return Modifier.semantics { customActions = list }
}

/**
 * Slides the item from its previous place to the new one whenever the layout moves it (a neighbour was dragged
 * past it). The [isActive] item follows the finger instead, so it jumps straight to its new slot.
 */
@Composable
private fun rememberPlacementAnimation(isActive: Boolean): Modifier {
    val scope = rememberCoroutineScope()
    var target by remember { mutableStateOf<IntOffset?>(null) }
    var animatable by remember { mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null) }
    return Modifier.onPlaced { coordinates -> target = coordinates.positionInParent().round() }
        .offset {
            val place = target ?: return@offset IntOffset.Zero
            val anim = animatable ?: Animatable(place, IntOffset.VectorConverter).also { animatable = it }
            when {
                isActive -> {
                    if (anim.targetValue != place || anim.value != place) scope.launch { anim.snapTo(place) }
                    IntOffset.Zero
                }
                anim.targetValue != place -> {
                    scope.launch { anim.animateTo(place, spring(stiffness = Spring.StiffnessMediumLow)) }
                    anim.value - place
                }
                else -> anim.value - place
            }
        }
}
