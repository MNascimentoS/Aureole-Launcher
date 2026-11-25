package dev.mnascimentos.aureole.feature.home.model

import kotlin.math.abs
import kotlin.math.hypot

/** Vertical travel, in dp, after which a swipe down opens the notification shade (D-05). */
const val SWIPE_DOWN_OPEN_DP = 48

/** What a finger that came down on the home is doing, decided once it travelled the touch slop (RF-06). */
enum class GestureLock { UNDECIDED, VERTICAL, HORIZONTAL }

/**
 * The direction a gesture locks to: [GestureLock.UNDECIDED] until the finger moved [slopPx], then vertical when
 * it moved [VERTICAL_GESTURE_RATIO] times more on y than on x, horizontal otherwise. The lock holds until the
 * finger lifts, so a horizontal page swipe never turns into a shade or a drawer.
 */
fun gestureLockOf(totalDx: Float, totalDy: Float, slopPx: Float): GestureLock = when {
    hypot(totalDx, totalDy) < slopPx -> GestureLock.UNDECIDED
    abs(totalDy) >= VERTICAL_GESTURE_RATIO * abs(totalDx) -> GestureLock.VERTICAL
    else -> GestureLock.HORIZONTAL
}
