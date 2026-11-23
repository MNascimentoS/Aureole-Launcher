package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockSpacingTest {

    private fun between(layout: ClockLayout) = clockSpacing(layout.lines).between

    @Test
    fun `top and bottom are the same edge room in every layout`() {
        ClockLayout.entries.forEach { layout ->
            val spacing = clockSpacing(layout.lines)
            assertEquals(layout.name, ClockSpace.EDGE, spacing.above)
            assertEquals(layout.name, spacing.above, spacing.below)
        }
    }

    @Test
    fun `the line above the time gets more room and the line below it less`() {
        assertEquals(listOf(ClockSpace.WIDER, ClockSpace.TIGHTER), between(ClockLayout.MSG_TIME_DATE))
        assertEquals(listOf(ClockSpace.WIDER, ClockSpace.TIGHTER), between(ClockLayout.DATE_TIME_MSG))
        assertEquals(listOf(ClockSpace.WIDER), between(ClockLayout.DATE_ON_TOP))
        assertEquals(listOf(ClockSpace.WIDER), between(ClockLayout.MSG_ON_TOP))
        assertEquals(listOf(ClockSpace.TIGHTER), between(ClockLayout.TIME_ON_TOP))
        assertEquals(listOf(ClockSpace.TIGHTER), between(ClockLayout.MSG_ON_BOTTOM))
    }

    @Test
    fun `time only has no space between lines`() {
        assertEquals(emptyList<ClockSpace>(), between(ClockLayout.TIME_ONLY))
    }

    @Test
    fun `a hidden message leaves the spaces of the lines still shown`() {
        val shown = ClockLayout.MSG_TIME_DATE.lines.filter { it != ClockLine.MESSAGE }

        assertEquals(listOf(ClockSpace.TIGHTER), clockSpacing(shown).between)
    }

    @Test
    fun `two lines away from the time have no space of their own`() {
        assertEquals(listOf(ClockSpace.NONE), clockSpacing(listOf(ClockLine.MESSAGE, ClockLine.DATE)).between)
    }

    @Test
    fun `at the large size, the spaces are the tokens of seletores-relogio D-10`() {
        val metrics = clockMetrics(68f)

        assertEquals(16f, metrics.gapDp(ClockSpace.EDGE), 0.01f)
        assertEquals(4f, metrics.gapDp(ClockSpace.WIDER), 0.01f)
        assertEquals(-4f, metrics.gapDp(ClockSpace.TIGHTER), 0.01f)
        assertEquals(0f, metrics.gapDp(ClockSpace.NONE), 0.01f)
        assertEquals(24f, metrics.radiusDp, 0.01f)
        assertEquals(15f, metrics.greetingSp, 0.01f)
        assertEquals(14f, metrics.dateSp, 0.01f)
    }

    @Test
    fun `above the floors, every size keeps the same share of the time`() {
        val large = clockMetrics(68f)
        val medium = clockMetrics(56f)
        val share = 56f / 68f

        assertEquals(large.greetingSp * share, medium.greetingSp, 0.01f)
        assertEquals(large.dateSp * share, medium.dateSp, 0.01f)
        assertEquals(large.edgeDp * share, medium.edgeDp, 0.01f)
        assertEquals(large.radiusDp * share, medium.radiusDp, 0.01f)
        assertEquals(large.widerGapDp * share, medium.widerGapDp, 0.01f)
        assertEquals(large.tighterGapDp * share, medium.tighterGapDp, 0.01f)
    }

    @Test
    fun `in small cells, the text and the edge never go under the legibility floors`() {
        listOf(12f, 20f, 30f, 42f).forEach { time ->
            val metrics = clockMetrics(time)

            assertTrue("t=$time", metrics.greetingSp >= CLOCK_MIN_TEXT_SP)
            assertTrue("t=$time", metrics.dateSp >= CLOCK_MIN_TEXT_SP)
            assertTrue("t=$time", metrics.edgeDp >= CLOCK_MIN_EDGE_DP)
            assertTrue("t=$time", metrics.radiusDp >= CLOCK_MIN_RADIUS_DP)
        }
    }

    @Test
    fun `the date is pulled up less as the time shrinks, and never pushed away`() {
        val pulls = (12..68 step 4).map { -clockMetrics(it.toFloat()).tighterGapDp }

        pulls.forEach { assertTrue(it >= 0f) }
        pulls.zipWithNext { smaller, bigger -> assertTrue(bigger >= smaller) }
        assertEquals("no pull at the smallest time, where the room is gone", 0f, pulls.first(), 0.01f)
        assertTrue("below the share at small sizes", pulls[2] < 20f * 4f / 68f)
    }
}
