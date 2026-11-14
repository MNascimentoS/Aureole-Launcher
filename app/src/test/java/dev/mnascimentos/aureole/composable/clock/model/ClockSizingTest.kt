package dev.mnascimentos.aureole.composable.clock.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockSizingTest {

    @Test
    fun `when the container is the onboarding 10x4 without room for a greeting, should use the normal size`() {
        val sizing = chooseClockSizing(maxHeightDp = 90f, clockStyle = "GREETING_AND_DATE")

        assertFalse(sizing.showGreeting)
        assertEquals(56f, sizing.timeSizePx)
    }

    @Test
    fun `when the container is tall with a greeting, should use the large size`() {
        val sizing = chooseClockSizing(maxHeightDp = 160f, clockStyle = "GREETING_AND_DATE")

        assertTrue(sizing.showGreeting)
        assertEquals(68f, sizing.timeSizePx)
    }

    @Test
    fun `when the container is at the minimum, should use the small size`() {
        assertEquals(42f, chooseClockSizing(maxHeightDp = 50f, clockStyle = "DATE_ON_TOP").timeSizePx)
        assertEquals(42f, chooseClockSizing(maxHeightDp = 10f, clockStyle = "TIME_ONLY").timeSizePx)
    }

    @Test
    fun `when the style is time only or date on top, should never show the greeting`() {
        assertFalse(chooseClockSizing(200f, "TIME_ONLY").showGreeting)
        assertFalse(chooseClockSizing(200f, "DATE_ON_TOP").showGreeting)
    }

    @Test
    fun `when the style asks for a greeting but the height is under 100dp, should hide it`() {
        assertFalse(chooseClockSizing(99f, "CUSTOM_GREETING").showGreeting)
        assertTrue(chooseClockSizing(100f, "CUSTOM_GREETING").showGreeting)
    }

    @Test
    fun `when the time block would not fit, should step down a size`() {
        // 68*0.95 + 20 = 84.6 does not fit in 80; 56*0.95 + 20 = 73.2 does.
        assertEquals(56f, chooseClockSizing(80f, "DATE_ON_TOP").timeSizePx)
    }
}
