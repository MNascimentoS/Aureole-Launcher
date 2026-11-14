package dev.mnascimentos.aureole.composable.clock.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockSizingTest {

    @Test
    fun `when the container is the onboarding 10x4, should use the normal size with the date and no greeting`() {
        val sizing = resolveClockSizing(maxHeightDp = 60f, fontScale = 1f, greetingAllowed = true)

        assertEquals(56f, sizing.textSizePx)
        assertFalse(sizing.showGreeting)
    }

    @Test
    fun `when the container is tall, should use the large size with the greeting`() {
        val sizing = resolveClockSizing(maxHeightDp = 160f, fontScale = 1f, greetingAllowed = true)

        assertEquals(68f, sizing.textSizePx)
        assertTrue(sizing.showGreeting)
    }

    @Test
    fun `when the container is at the minimum, should use the small size`() {
        val sizing = resolveClockSizing(maxHeightDp = 40f, fontScale = 1f, greetingAllowed = true)

        assertEquals(42f, sizing.textSizePx)
        assertFalse(sizing.showGreeting)
    }

    @Test
    fun `when the style does not allow a greeting, should never show it`() {
        assertFalse(resolveClockSizing(200f, 1f, greetingAllowed = false).showGreeting)
    }

    @Test
    fun `when the font scale grows, should step the size down or drop the greeting`() {
        val normal = resolveClockSizing(maxHeightDp = 100f, fontScale = 1f, greetingAllowed = true)
        val scaled = resolveClockSizing(maxHeightDp = 100f, fontScale = 1.3f, greetingAllowed = true)

        assertTrue(normal.textSizePx > scaled.textSizePx || normal.showGreeting && !scaled.showGreeting)
    }

    @Test
    fun `when there is no date, should not reserve room for it`() {
        assertEquals(68f, resolveClockSizing(50f, 1f, greetingAllowed = false, hasDate = false).textSizePx)
        assertEquals(42f, resolveClockSizing(50f, 1f, greetingAllowed = false, hasDate = true).textSizePx)
    }
}
