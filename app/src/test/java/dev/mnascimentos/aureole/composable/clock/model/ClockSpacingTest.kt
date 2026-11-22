package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockLine
import org.junit.Assert.assertEquals
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
}
