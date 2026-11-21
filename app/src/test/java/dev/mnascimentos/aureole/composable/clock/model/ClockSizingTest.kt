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
    fun `when the smallest fixed size fits, should use it`() {
        val sizing = resolveClockSizing(maxHeightDp = 50f, fontScale = 1f, greetingAllowed = true)

        assertEquals(42f, sizing.textSizePx)
        assertFalse(sizing.showGreeting)
    }

    @Test
    fun `when the container is a short landscape cell, should size the time by the height left by the date`() {
        val sizing = resolveClockSizing(maxHeightDp = 40f, fontScale = 1f, greetingAllowed = true)

        assertTrue(sizing.textSizePx < 42f)
        assertFalse(sizing.showGreeting)
        assertTrue(sizing.blockHeightDp <= 40f)
    }

    @Test
    fun `when in landscape heights, should keep the lines inside the height with no negative room`() {
        listOf(36f, 40f, 44f, 48f, 56f, 64f, 72f).forEach { height ->
            listOf(1f, 1.15f, 1.3f).forEach { fontScale ->
                listOf(true, false).forEach { hasDate ->
                    val sizing = resolveClockSizing(height, fontScale, greetingAllowed = true, hasDate = hasDate)

                    assertTrue("h=$height scale=$fontScale date=$hasDate", sizing.blockHeightDp <= height)
                    assertTrue(height - sizing.blockHeightDp >= 0f)
                    assertTrue(sizing.textSizePx > 0f)
                }
            }
        }
    }

    @Test
    fun `when the greeting is shown, should count it in the lines height`() {
        val sizing = resolveClockSizing(maxHeightDp = 160f, fontScale = 1f, greetingAllowed = true)
        val withoutGreeting = resolveClockSizing(maxHeightDp = 160f, fontScale = 1f, greetingAllowed = false)

        assertTrue(sizing.blockHeightDp > withoutGreeting.blockHeightDp)
        assertTrue(sizing.blockHeightDp <= 160f)
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
