package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubberEdgeGestureTest {

    private val slop = 8f

    @Test
    fun `hold is between 150 and 200 ms`() {
        assertTrue(EDGE_HOLD_MS in 150L..200L)
    }

    @Test
    fun `finger resting within the slop keeps waiting`() {
        assertEquals(
            EdgePressOutcome.WAITING,
            edgePressOutcome(EdgePressSample(isPressed = true, travelPx = 3f, touchSlopPx = slop))
        )
    }

    @Test
    fun `finger still down when the hold runs out starts the scrub`() {
        assertEquals(
            EdgePressOutcome.SCRUB,
            edgePressOutcome(
                EdgePressSample(isPressed = true, travelPx = 0f, touchSlopPx = slop, hasHoldElapsed = true)
            )
        )
    }

    @Test
    fun `moving past the slop starts the scrub before the hold`() {
        assertEquals(
            EdgePressOutcome.SCRUB,
            edgePressOutcome(EdgePressSample(isPressed = true, travelPx = 9f, touchSlopPx = slop))
        )
        assertEquals(
            EdgePressOutcome.WAITING,
            edgePressOutcome(EdgePressSample(isPressed = true, travelPx = slop, touchSlopPx = slop))
        )
    }

    @Test
    fun `lifting before the hold is a plain tap`() {
        assertEquals(
            EdgePressOutcome.TAP,
            edgePressOutcome(EdgePressSample(isPressed = false, travelPx = 0f, touchSlopPx = slop))
        )
        assertEquals(
            EdgePressOutcome.TAP,
            edgePressOutcome(EdgePressSample(isPressed = false, travelPx = 40f, touchSlopPx = slop, isConsumed = true))
        )
    }

    @Test
    fun `movement taken by a child under the finger yields the gesture`() {
        assertEquals(
            EdgePressOutcome.YIELD,
            edgePressOutcome(EdgePressSample(isPressed = true, travelPx = 40f, touchSlopPx = slop, isConsumed = true))
        )
    }

    @Test
    fun `home drag stays attached while an edge scrub runs over the drawer`() {
        assertTrue(isHomeDragAttached(isOverlayActive = false, isEdgeScrubbing = false))
        assertTrue(isHomeDragAttached(isOverlayActive = true, isEdgeScrubbing = true))
        assertTrue(isHomeDragAttached(isOverlayActive = false, isEdgeScrubbing = true))
        assertFalse(isHomeDragAttached(isOverlayActive = true, isEdgeScrubbing = false))
    }
}
