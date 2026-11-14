package dev.mnascimentos.aureole.feature.home.components.scrubber.model

/** How long a finger resting on the alphabet edge of the home waits before the drawer opens on its letter. */
const val EDGE_HOLD_MS = 180L

/** What a touch that started on the alphabet edge of the home turned into. */
enum class EdgePressOutcome {
    /** Still down, still within the touch slop and the hold has not run out yet. */
    WAITING,

    /** Held long enough or moved past the touch slop: open the drawer and scrub with the finger. */
    SCRUB,

    /** Lifted before the hold: a plain tap, the edge does nothing. */
    TAP,

    /** Something under the finger (a scrolling widget) took the movement: leave the gesture to it. */
    YIELD
}

/** One reading of the finger while the edge waits to decide. */
data class EdgePressSample(
    val isPressed: Boolean,
    val travelPx: Float,
    val touchSlopPx: Float,
    val isConsumed: Boolean = false,
    val hasHoldElapsed: Boolean = false
)

/** The edge decision: a tap stays a tap, a hold or a move past the touch slop starts the scrub. */
fun edgePressOutcome(sample: EdgePressSample): EdgePressOutcome = when {
    !sample.isPressed -> EdgePressOutcome.TAP
    sample.isConsumed -> EdgePressOutcome.YIELD
    sample.hasHoldElapsed || sample.travelPx > sample.touchSlopPx -> EdgePressOutcome.SCRUB
    else -> EdgePressOutcome.WAITING
}

/** The home drag modifier stays on while an edge scrub runs, even after the drawer it opened covers the home. */
fun isHomeDragAttached(isOverlayActive: Boolean, isEdgeScrubbing: Boolean): Boolean =
    !isOverlayActive || isEdgeScrubbing
