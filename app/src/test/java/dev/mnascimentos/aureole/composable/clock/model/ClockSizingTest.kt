package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockSizingTest {

    private val full = ClockLayout.MSG_TIME_DATE.lines
    private val wide = 300f

    @Test
    fun `when the card is tall and wide, should use the large size with the greeting`() {
        val sizing = resolveClockSizing(full, maxTextWidthDp = wide, maxHeightDp = 200f)

        assertEquals(CLOCK_MAX_TIME_SP, sizing.textSizePx)
        assertTrue(sizing.showGreeting)
    }

    @Test
    fun `when the room is unbounded in height, should use the large size`() {
        val sizing = resolveClockSizing(full, maxTextWidthDp = wide, maxHeightDp = Float.POSITIVE_INFINITY)

        assertEquals(CLOCK_MAX_TIME_SP, sizing.textSizePx)
        assertTrue(sizing.showGreeting)
    }

    @Test
    fun `when the card is narrow, should size the time by the width`() {
        val narrow = resolveClockSizing(full, maxTextWidthDp = 110f, maxHeightDp = 200f)

        assertTrue(narrow.textSizePx < CLOCK_MAX_TIME_SP)
        assertTrue(narrow.textSizePx > CLOCK_MIN_TIME_SP)
        assertTrue("same layout as the wide card", narrow.showGreeting)
    }

    @Test
    fun `when the height grows, should grow the time continuously, not in steps`() {
        val sizes = (60..140 step 4).map { resolveClockSizing(full, wide, it.toFloat()).textSizePx }

        sizes.zipWithNext { lower, higher -> assertTrue("$lower > $higher", higher >= lower) }
        assertTrue("continuous sizes", sizes.distinct().size > 8)
    }

    @Test
    fun `in small, medium and large cells, should keep the card inside the room in every layout`() {
        listOf(48f, 64f, 80f, 96f, 120f, 160f).forEach { height ->
            listOf(110f, 200f, wide).forEach { width ->
                listOf(1f, 1.15f, 1.3f).forEach { fontScale ->
                    ClockLayout.entries.forEach { layout ->
                        val sizing = resolveClockSizing(layout.lines, width, height, fontScale)
                        val tag = "h=$height w=$width scale=$fontScale ${layout.name}"

                        // At the floor (a 48 dp cell with a large font) not even the smallest card fits: the time
                        // stays at the floor and the card clips what is left (`resolveClockSizing`).
                        val isAtFloor = sizing.textSizePx <= CLOCK_MIN_TIME_SP + 0.01f
                        assertTrue(tag, isAtFloor || sizing.blockHeightDp <= height + 1f)
                        assertTrue(tag, sizing.textSizePx >= CLOCK_MIN_TIME_SP)
                    }
                }
            }
        }
    }

    @Test
    fun `when the card is drawn in the height it was measured at, should give the same sizes`() {
        listOf(110f, 200f, wide).forEach { width ->
            ClockLayout.entries.forEach { layout ->
                val home = resolveClockSizing(layout.lines, width, Float.POSITIVE_INFINITY)
                val preview = resolveClockSizing(layout.lines, width, home.blockHeightDp)

                assertEquals(layout.name, home.textSizePx, preview.textSizePx, 0.5f)
                assertEquals(layout.name, home.showGreeting, preview.showGreeting)
            }
        }
    }

    @Test
    fun `when the room is too short for a greeting, should drop it and keep the time`() {
        val sizing = resolveClockSizing(full, maxTextWidthDp = wide, maxHeightDp = 80f)

        assertFalse(sizing.showGreeting)
        assertTrue(sizing.textSizePx > CLOCK_MIN_TIME_SP)
    }

    @Test
    fun `when the layout has no greeting, should never show it`() {
        assertFalse(resolveClockSizing(ClockLayout.TIME_ONLY.lines, wide, 400f).showGreeting)
        assertFalse(resolveClockSizing(ClockLayout.TIME_ON_TOP.lines, wide, 400f).showGreeting)
    }

    @Test
    fun `when there is no date, should give the time the room of the date`() {
        val withDate = resolveClockSizing(ClockLayout.TIME_ON_TOP.lines, wide, 70f)
        val timeOnly = resolveClockSizing(ClockLayout.TIME_ONLY.lines, wide, 70f)

        assertTrue(timeOnly.textSizePx > withDate.textSizePx)
    }

    @Test
    fun `when the font scale grows, should shrink the time or drop the greeting`() {
        val normal = resolveClockSizing(full, wide, 120f, fontScale = 1f)
        val scaled = resolveClockSizing(full, wide, 120f, fontScale = 1.3f)

        assertTrue(normal.textSizePx > scaled.textSizePx || normal.showGreeting && !scaled.showGreeting)
    }

    @Test
    fun `when the greeting is shown, should count it in the card height`() {
        val time = resolveClockSizing(full, wide, 200f).textSizePx

        assertTrue(clockCardHeightDp(full, time) > clockCardHeightDp(ClockLayout.TIME_ON_TOP.lines, time))
    }

    @Test
    fun `when the card is smaller than the floor, should stop at the floor`() {
        assertEquals(CLOCK_MIN_TIME_SP, resolveClockSizing(full, 20f, 10f).textSizePx)
    }
}
