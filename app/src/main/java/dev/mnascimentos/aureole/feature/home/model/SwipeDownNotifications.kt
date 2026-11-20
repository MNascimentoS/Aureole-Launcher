package dev.mnascimentos.aureole.feature.home.model

import kotlin.math.abs

/** Vertical travel, in dp, before a home swipe counts as up or down. */
const val VERTICAL_INTENT_THRESHOLD_DP = 36

/** A swipe is vertical when it moves this many times more on y than on x. */
const val VERTICAL_GESTURE_RATIO = 1.5f

enum class VerticalSwipe { NONE, UP, DOWN }

/** Up or down once the finger travelled [thresholdPx] mostly vertically; [NONE] while it has not. */
fun verticalSwipeOf(totalDx: Float, totalDy: Float, thresholdPx: Float): VerticalSwipe {
    val absY = abs(totalDy)
    val isVertical = absY > thresholdPx && absY > VERTICAL_GESTURE_RATIO * abs(totalDx)
    return when {
        !isVertical -> VerticalSwipe.NONE
        totalDy > 0 -> VerticalSwipe.DOWN
        else -> VerticalSwipe.UP
    }
}

/**
 * What decides whether a swipe down on the home opens the notification shade (RF-B1, RF-B2). The home grid itself
 * never scrolls, so "at the top" is about what is under the finger: [hasScrolledContent] is true once something
 * scrollable (the apps list, a scrolling group, a scrollable widget) moved during the gesture, that is, it could
 * still scroll up when the finger came down.
 */
data class SwipeDownConditions(
    /** Settings › Behavior "Swipe down for notifications". */
    val isEnabled: Boolean,
    val hasScrolledContent: Boolean = false,
    /** A sheet, folder, dialog, the drawer or a blocking onboarding step is in front of the home. */
    val isOverlayActive: Boolean = false,
    /** Grid edit mode, where dragging moves items. */
    val isEditing: Boolean = false,
    /** The finger came down on the alphabet edge, which scrubs instead. */
    val isEdgeGesture: Boolean = false
)

fun SwipeDownConditions.opensNotifications(): Boolean =
    isEnabled && !hasScrolledContent && !isOverlayActive && !isEditing && !isEdgeGesture

/**
 * Follows the nested scroll of one gesture over the home: a swipe down that starts on a list already at its top
 * reaches the home only as the delta the list left. Opens once per gesture, after [thresholdPx] of that delta, and
 * never when the list scrolled first. [reset] runs on every new finger and at the end of the drag.
 */
class SwipeDownScrollTracker(private val thresholdPx: Float) {
    private var leftoverDown = 0f
    private var hasOpened = false

    var hasScrolledContent: Boolean = false
        private set

    fun reset() {
        leftoverDown = 0f
        hasOpened = false
        hasScrolledContent = false
    }

    /** One nested scroll step: [consumedY] by the content under the finger, [availableY] left for the home. */
    fun onPostScroll(consumedY: Float, availableY: Float): Boolean {
        if (consumedY != 0f) hasScrolledContent = true
        if (hasOpened || hasScrolledContent || availableY <= 0f) return false
        leftoverDown += availableY
        hasOpened = leftoverDown > thresholdPx
        return hasOpened
    }
}
