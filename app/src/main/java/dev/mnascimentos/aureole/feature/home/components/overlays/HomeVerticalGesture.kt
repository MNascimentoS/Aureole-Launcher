package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.home.model.GestureLock
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.SWIPE_DOWN_OPEN_DP
import dev.mnascimentos.aureole.feature.home.model.VerticalGestureState
import dev.mnascimentos.aureole.feature.home.model.drawerProgressOf
import dev.mnascimentos.aureole.feature.home.model.gestureLockOf

/**
 * One drag step of a vertical gesture over the home: locks the direction at the touch slop, then a swipe down opens the
 * shade and a swipe up pulls the drawer, each only when it is on.
 */
internal fun handleVerticalGesture(
    change: PointerInputChange,
    total: Offset,
    gesture: VerticalGestureState,
    params: HomeDragParams,
    touchSlopPx: Float
) {
    if (params.isAllAppsDrawerOpen || change.isConsumed) return
    if (gesture.lock == GestureLock.UNDECIDED) gesture.lock = gestureLockOf(total.x, total.y, touchSlopPx)
    if (gesture.lock != GestureLock.VERTICAL) return
    if (gesture.hasTriggered) {
        followVerticalGesture(change, total, gesture, params)
    } else {
        startVerticalGesture(change, total, gesture, params)
    }
}

private fun followVerticalGesture(
    change: PointerInputChange,
    total: Offset,
    gesture: VerticalGestureState,
    params: HomeDragParams
) {
    change.consume()
    if (!gesture.isDrawerDrag) return
    gesture.progress = drawerProgressOf(-total.y, params.screenHeightPx)
    params.drawerDrag?.onProgress?.invoke(gesture.progress)
}

/** The shade opens past [SWIPE_DOWN_OPEN_DP] while the finger is still down, once per gesture (D-05). */
private fun startVerticalGesture(
    change: PointerInputChange,
    total: Offset,
    gesture: VerticalGestureState,
    params: HomeDragParams
) {
    val isUp = total.y < 0f
    val downOpenPx = with(params.density) { SWIPE_DOWN_OPEN_DP.dp.toPx() }
    // With a swipe off, it does nothing and leaves the finger to the home.
    val canStart = if (isUp) params.isSwipeUpEnabled else params.isSwipeDownEnabled && total.y >= downOpenPx
    if (!canStart) return
    gesture.hasTriggered = true
    change.consume()
    when {
        !isUp -> params.onExpandNotificationShade()
        params.drawerDrag != null -> {
            gesture.isDrawerDrag = true
            gesture.progress = drawerProgressOf(-total.y, params.screenHeightPx)
            params.drawerDrag.onProgress(gesture.progress)
        }
        else -> params.onAllAppsDrawerOpen()
    }
}
