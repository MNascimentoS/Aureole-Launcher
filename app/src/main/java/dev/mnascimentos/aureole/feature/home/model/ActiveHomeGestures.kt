package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

/** Width, in dp, of the strip at each side of the screen that belongs to the system back gesture (RF-07). */
const val SYSTEM_GESTURE_EDGE_DP = 20

/** Height, in dp, of the strip at the bottom that belongs to the system navigation gesture (RF-07). */
const val SYSTEM_GESTURE_BOTTOM_DP = 32

/** Which vertical swipes of the home are on, from the two Settings › Behavior toggles (RF-02). */
data class ActiveHomeGestures(val isSwipeDownOn: Boolean, val isSwipeUpOn: Boolean) {
    /** The vertical detector is installed only when at least one swipe is on: otherwise the home sees every touch. */
    val isAnyOn: Boolean get() = isSwipeDownOn || isSwipeUpOn
}

fun MainUiState.activeHomeGestures() = ActiveHomeGestures(
    isSwipeDownOn = isSwipeDownNotificationsEnabled,
    isSwipeUpOn = isSwipeUpAllAppsEnabled
)

/** True when a touch that came down at [position] is on a strip the system gestures own: a swipe there never opens. */
fun isInSystemGestureZone(position: Offset, screen: Size, edgePx: Float, bottomPx: Float): Boolean =
    position.x < edgePx || position.x > screen.width - edgePx || position.y > screen.height - bottomPx

/** [isInSystemGestureZone] with the strips of [SYSTEM_GESTURE_EDGE_DP] and [SYSTEM_GESTURE_BOTTOM_DP] on [density]. */
fun HomeDragParams.isInSystemZone(position: Offset): Boolean = isInSystemGestureZone(
    position = position,
    screen = Size(screenWidthPx, screenHeightPx),
    edgePx = with(density) { SYSTEM_GESTURE_EDGE_DP.dp.toPx() },
    bottomPx = with(density) { SYSTEM_GESTURE_BOTTOM_DP.dp.toPx() }
)
