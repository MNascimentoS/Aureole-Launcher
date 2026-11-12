package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.EDGE_HOLD_MS
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.EdgePressOutcome
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.EdgePressSample
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberBounds
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberEdgeZone
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.edgePressOutcome
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBoundsFor
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import kotlin.math.abs

private const val VERTICAL_INTENT_THRESHOLD_DP = 36
private const val VERTICAL_GESTURE_RATIO = 1.5f

/**
 * Home gestures: a vertical swipe opens the drawer (up) or the notification shade (down); a touch that starts on the
 * alphabet edge, as tall as the letters, opens the drawer on the letter under the finger once it rests for
 * [EDGE_HOLD_MS] or moves past the touch slop, and keeps scrubbing until it lifts (RF-20).
 *
 * The edge detector is the inner modifier, so it sees each event before the vertical one and only what the
 * children under the finger left; the vertical one ignores gestures that started on the edge.
 */
fun Modifier.homeDragGestures(params: HomeDragParams): Modifier =
    homeVerticalGestures(params).homeEdgeScrubGestures(params)

/** The alphabet edge strip of the home, or null when the alphabet is off. */
internal fun HomeDragParams.scrubberEdgeZone(): ScrubberEdgeZone? {
    if (isAlphabetScrubberDisabled) return null
    return ScrubberEdgeZone(
        screenWidthPx = screenWidthPx,
        edgeWidthPx = with(density) { ScrubberDimensions.EdgeGestureStartWidth.toPx() },
        bounds = scrubberBoundsFor(screenHeightPx, density),
        isLeftHanded = isLeftHandedMode
    )
}

private fun Modifier.homeVerticalGestures(params: HomeDragParams): Modifier = pointerInput(
    params.isLeftHandedMode,
    params.isAlphabetScrubberDisabled,
    params.screenHeightPx,
    params.screenWidthPx
) {
    val edgeZone = params.scrubberEdgeZone()
    var isEdgeGesture = false
    var hasTriggeredGesture = false
    var totalDragOffset = Offset.Zero

    detectDragGestures(
        orientationLock = null,
        onDragStart = { down, _, _ ->
            totalDragOffset = Offset.Zero
            hasTriggeredGesture = false
            isEdgeGesture = edgeZone?.contains(down.position.x, down.position.y) == true
        },
        onDrag = { change, dragAmount ->
            totalDragOffset += dragAmount
            if (!isEdgeGesture) {
                hasTriggeredGesture = handleVerticalGesture(
                    change = change,
                    totalDx = totalDragOffset.x,
                    totalDy = totalDragOffset.y,
                    hasTriggered = hasTriggeredGesture,
                    params = params
                )
            }
        }
    )
}

private fun Modifier.homeEdgeScrubGestures(params: HomeDragParams): Modifier = pointerInput(
    params.isLeftHandedMode,
    params.isAlphabetScrubberDisabled,
    params.screenHeightPx,
    params.screenWidthPx
) {
    val edgeZone = params.scrubberEdgeZone() ?: return@pointerInput
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (!edgeZone.contains(down.position.x, down.position.y)) return@awaitEachGesture
        val start = awaitEdgeScrubStart(down) ?: return@awaitEachGesture
        runEdgeScrub(pointerId = down.id, startY = start.y, bounds = edgeZone.bounds, params = params)
    }
}

/** Waits for the hold or the touch slop; the position the scrub starts from, or null for a tap or a yield. */
private suspend fun AwaitPointerEventScope.awaitEdgeScrubStart(down: PointerInputChange): Offset? {
    val touchSlopPx = viewConfiguration.touchSlop
    var position = down.position
    var outcome = EdgePressOutcome.WAITING
    withTimeoutOrNull(EDGE_HOLD_MS) {
        while (outcome == EdgePressOutcome.WAITING) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
            if (change != null) position = change.position
            outcome = edgePressOutcome(
                EdgePressSample(
                    isPressed = change?.pressed == true,
                    travelPx = (position - down.position).getDistance(),
                    touchSlopPx = touchSlopPx,
                    isConsumed = change?.isConsumed == true
                )
            )
            if (outcome == EdgePressOutcome.SCRUB) change?.consume()
        }
    }
    if (outcome == EdgePressOutcome.WAITING) {
        outcome = edgePressOutcome(
            EdgePressSample(isPressed = true, travelPx = 0f, touchSlopPx = touchSlopPx, hasHoldElapsed = true)
        )
    }
    return position.takeIf { outcome == EdgePressOutcome.SCRUB }
}

/**
 * Opens the drawer and hands the finger to its alphabet until it lifts. Events are taken in the initial pass, so the
 * home items under the finger see them consumed and drop their click or long press.
 */
private suspend fun AwaitPointerEventScope.runEdgeScrub(
    pointerId: PointerId,
    startY: Float,
    bounds: ScrubberBounds,
    params: HomeDragParams
) {
    params.onEdgeScrubChange(true)
    try {
        if (!params.isAllAppsDrawerOpen) params.onAllAppsDrawerOpen()
        params.onExternalTouchYChange(bounds.toLocalY(startY))
        var isPressed = true
        while (isPressed) {
            val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == pointerId }
            isPressed = change?.pressed == true
            change?.consume()
            if (change != null && isPressed) params.onExternalTouchYChange(bounds.toLocalY(change.position.y))
        }
    } finally {
        params.onExternalTouchYChange(-1f)
        params.onEdgeScrubChange(false)
    }
}

private fun handleVerticalGesture(
    change: PointerInputChange,
    totalDx: Float,
    totalDy: Float,
    hasTriggered: Boolean,
    params: HomeDragParams
): Boolean {
    if (params.isAllAppsDrawerOpen || change.isConsumed) {
        return hasTriggered
    }

    var triggered = hasTriggered
    if (!triggered) {
        val verticalIntentThresholdPx = with(params.density) { VERTICAL_INTENT_THRESHOLD_DP.dp.toPx() }
        val absX = abs(totalDx)
        val absY = abs(totalDy)

        if (absY > verticalIntentThresholdPx && absY > VERTICAL_GESTURE_RATIO * absX) {
            triggered = true
            change.consume()
            if (totalDy > 0) {
                params.onExpandNotificationShade()
            } else {
                params.onAllAppsDrawerOpen()
            }
        }
    } else {
        change.consume()
    }

    return triggered
}
