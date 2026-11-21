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

/** Whether this swipe opens something on the home: up opens the drawer, down the shade, each with its own toggle. */
fun VerticalSwipe.opensHome(isUpEnabled: Boolean, isDownEnabled: Boolean): Boolean = when (this) {
    VerticalSwipe.UP -> isUpEnabled
    VerticalSwipe.DOWN -> isDownEnabled
    VerticalSwipe.NONE -> false
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
 * What decides whether a swipe up on the home opens the all apps drawer through the nested scroll: the mirror of
 * [SwipeDownConditions]. A scrollable under the finger (a scrolling group, a scrollable widget) takes the swipe while
 * it can still scroll; once it sits at its end, the delta it leaves opens the drawer.
 */
data class SwipeUpConditions(
    /** Settings › Behavior "Swipe up for all apps". */
    val isEnabled: Boolean,
    val hasScrolledContent: Boolean = false,
    /** A sheet, folder, dialog, the drawer or a blocking onboarding step is in front of the home. */
    val isOverlayActive: Boolean = false,
    /** Grid edit mode, where dragging moves items. */
    val isEditing: Boolean = false,
    val isEdgeGesture: Boolean = false
)

fun SwipeUpConditions.opensAllApps(): Boolean =
    isEnabled && !hasScrolledContent && !isOverlayActive && !isEditing && !isEdgeGesture

/**
 * Follows the nested scroll of one gesture over the home: a swipe in [direction] that starts on a scrollable already
 * at that end reaches the home only as the delta the scrollable left (down for a list at its top, up for one at its
 * bottom). Opens once per gesture, after [thresholdPx] of that delta, and never when the scrollable scrolled first.
 * [reset] runs on every new finger and at the end of the drag.
 */
class SwipeScrollTracker(private val thresholdPx: Float, private val direction: VerticalSwipe = VerticalSwipe.DOWN) {
    private var leftover = 0f
    private var hasOpened = false

    var hasScrolledContent: Boolean = false
        private set

    fun reset() {
        leftover = 0f
        hasOpened = false
        hasScrolledContent = false
    }

    /** One nested scroll step: [consumedY] by the content under the finger, [availableY] left for the home. */
    fun onPostScroll(consumedY: Float, availableY: Float): Boolean {
        if (consumedY != 0f) hasScrolledContent = true
        val inDirection = if (direction == VerticalSwipe.DOWN) availableY else -availableY
        if (hasOpened || hasScrolledContent || inDirection <= 0f) return false
        leftover += inDirection
        hasOpened = leftover > thresholdPx
        return hasOpened
    }
}
