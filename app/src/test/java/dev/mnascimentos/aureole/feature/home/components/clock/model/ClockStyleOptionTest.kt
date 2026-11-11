package dev.mnascimentos.aureole.feature.home.components.clock.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClockStyleOptionTest {

    @Test
    fun `when laying out the picker, should show the three main styles first`() {
        val first = ClockStyleOption.rows.first()

        assertEquals(
            listOf(ClockStyleOption.DATE_ON_TOP, ClockStyleOption.TIME_ONLY, ClockStyleOption.GREETING_AND_DATE),
            first
        )
        assertEquals(ClockStyleOption.entries, ClockStyleOption.rows.flatten())
    }

    @Test
    fun `when looking a style up by its stored key, should find it and ignore unknown keys`() {
        ClockStyleOption.entries.forEach { assertEquals(it, ClockStyleOption.fromKey(it.key)) }
        assertNull(ClockStyleOption.fromKey("NOPE"))
    }

    @Test
    fun `when drawing miniatures, should draw the time block once in every style`() {
        ClockStyleOption.entries.forEach { option ->
            assertEquals(1, option.lines.count { it == ClockStyleLine.LARGE })
        }
    }

    @Test
    fun `when finding the alignment segment, should map the stored key and return -1 for unknown`() {
        assertEquals(0, ClockAlignmentOption.indexOfKey("START"))
        assertEquals(1, ClockAlignmentOption.indexOfKey("CENTER"))
        assertEquals(2, ClockAlignmentOption.indexOfKey("END"))
        assertEquals(-1, ClockAlignmentOption.indexOfKey("RIGHT"))
    }
}
