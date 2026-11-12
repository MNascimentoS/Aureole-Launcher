package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubberEdgeTest {

    @Test
    fun `when right handed, should use the right inset`() {
        assertEquals(40, scrubberGestureInsetPx(isLeftHandedMode = false, leftInsetPx = 10, rightInsetPx = 40))
    }

    @Test
    fun `when left handed, should use the left inset`() {
        assertEquals(10, scrubberGestureInsetPx(isLeftHandedMode = true, leftInsetPx = 10, rightInsetPx = 40))
    }

    @Test
    fun `when button navigation leaves no inset, should not move the scrubber`() {
        assertEquals(0, scrubberGestureInsetPx(isLeftHandedMode = false, leftInsetPx = 0, rightInsetPx = 0))
    }

    @Test
    fun `when the inset is negative, should clamp it to zero`() {
        assertEquals(0, scrubberGestureInsetPx(isLeftHandedMode = true, leftInsetPx = -5, rightInsetPx = 3))
    }

    @Test
    fun `when computing the exclusion, should cover the whole touch area`() {
        assertEquals(Rect(0f, 0f, 48f, 300f), scrubberExclusionRect(widthPx = 48f, heightPx = 300f))
    }

    @Test
    fun `when the inset grows, should widen the start zone by the same amount`() {
        assertEquals(96f, scrubberEdgeStartZonePx(baseWidthPx = 64f, insetPx = 32f), 0f)
        assertEquals(64f, scrubberEdgeStartZonePx(baseWidthPx = 64f, insetPx = -8f), 0f)
    }

    @Test
    fun `when right handed, should start the scrub inside the right zone only`() {
        assertTrue(isInScrubberEdgeStartZone(x = 950f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = false))
        assertFalse(isInScrubberEdgeStartZone(x = 900f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = false))
        assertFalse(isInScrubberEdgeStartZone(x = 10f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = false))
    }

    @Test
    fun `when left handed, should start the scrub inside the left zone only`() {
        assertTrue(isInScrubberEdgeStartZone(x = 50f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = true))
        assertFalse(isInScrubberEdgeStartZone(x = 100f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = true))
        assertFalse(isInScrubberEdgeStartZone(x = 990f, screenWidthPx = 1000f, zonePx = 96f, isLeftHandedMode = true))
    }
}
