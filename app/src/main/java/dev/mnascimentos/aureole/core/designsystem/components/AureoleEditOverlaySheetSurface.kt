package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

private const val DISMISS_DRAG_THRESHOLD_PX = 50f
private const val DISMISS_VELOCITY_THRESHOLD = 1000f
private const val EXPAND_VELOCITY_THRESHOLD = -500f
private const val COLLAPSE_VELOCITY_THRESHOLD = 500f

@Composable
internal fun BoxScope.SheetSurface(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    bounds: SheetBounds
) {
    val dragExpansion = stateData.dragExpansion
    val onDragExpansionChanged = stateData.onDragExpansionChanged

    val nestedScrollConnection = remember(bounds.maxExpansionAllowed, dragExpansion, onDragExpansionChanged) {
        createNestedScrollConnection(bounds, dragExpansion, onDragExpansionChanged)
    }

    val draggableState = rememberSheetDraggableState(
        bounds = bounds,
        dragExpansion = dragExpansion,
        onDragExpansionChanged = onDragExpansionChanged,
        triggerDismiss = stateData.triggerDismiss
    )
    val currentMaxSheetHeight = bounds.maxHeight + dragExpansion.dp

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .heightIn(max = currentMaxSheetHeight)
            .nestedScroll(nestedScrollConnection)
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    handleDragStopped(
                        velocity = velocity,
                        dragExpansion = dragExpansion,
                        maxExpansionAllowed = bounds.maxExpansionAllowed,
                        triggerDismiss = stateData.triggerDismiss,
                        onExpansionChanged = onDragExpansionChanged
                    )
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = {}
            ),
        shape = RoundedCornerShape(
            topStart = AureoleDS.dimens.radiusLarge,
            topEnd = AureoleDS.dimens.radiusLarge
        ),
        color = AureoleDS.colors.surface,
        contentColor = AureoleDS.colors.onSurfaceMedium
    ) {
        OverlaySheetBody(
            title = params.config.title,
            onSettingsClick = params.config.onSettingsClick,
            bottomBarContent = params.bottomBarContent,
            sheetContent = params.sheetContent
        )
    }
}

@Composable
private fun rememberSheetDraggableState(
    bounds: SheetBounds,
    dragExpansion: Float,
    onDragExpansionChanged: (Float) -> Unit,
    triggerDismiss: () -> Unit
): DraggableState {
    return rememberDraggableState { delta ->
        val newExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
        onDragExpansionChanged(newExpansion)
        if ((newExpansion <= 0f) && (delta > DISMISS_DRAG_THRESHOLD_PX)) {
            triggerDismiss()
        }
    }
}

private fun createNestedScrollConnection(
    bounds: SheetBounds,
    dragExpansion: Float,
    onDragExpansionChanged: (Float) -> Unit
): NestedScrollConnection {
    return object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            if (delta < 0 && dragExpansion < bounds.maxExpansionAllowed.value) {
                val newExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
                onDragExpansionChanged(newExpansion)
                val consumedY = newExpansion - dragExpansion
                return Offset(0f, -consumedY)
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            if (delta > 0 && dragExpansion > 0f) {
                val newExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
                onDragExpansionChanged(newExpansion)
                val consumedY = dragExpansion - newExpansion
                return Offset(0f, consumedY)
            }
            return Offset.Zero
        }
    }
}

private fun handleDragStopped(
    velocity: Float,
    dragExpansion: Float,
    maxExpansionAllowed: Dp,
    triggerDismiss: () -> Unit,
    onExpansionChanged: (Float) -> Unit
) {
    if (velocity > DISMISS_VELOCITY_THRESHOLD && dragExpansion == 0f) {
        triggerDismiss()
        return
    }
    if (velocity < EXPAND_VELOCITY_THRESHOLD) {
        onExpansionChanged(maxExpansionAllowed.value)
        return
    }
    if (velocity > COLLAPSE_VELOCITY_THRESHOLD) {
        onExpansionChanged(0f)
        return
    }
    val targetExpansion = if (dragExpansion > maxExpansionAllowed.value / 2) {
        maxExpansionAllowed.value
    } else {
        0f
    }
    onExpansionChanged(targetExpansion)
}
