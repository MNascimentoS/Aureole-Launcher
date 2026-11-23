package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.data.model.ClockLine.DATE
import dev.mnascimentos.aureole.core.data.model.ClockLine.MESSAGE
import dev.mnascimentos.aureole.core.data.model.ClockLine.TIME
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockLayoutTest {

    @Test
    fun `when reading the lines of each layout, should follow the order of its name`() {
        assertEquals(listOf(TIME), ClockLayout.TIME_ONLY.lines)
        assertEquals(listOf(DATE, TIME), ClockLayout.DATE_ON_TOP.lines)
        assertEquals(listOf(TIME, DATE), ClockLayout.TIME_ON_TOP.lines)
        assertEquals(listOf(DATE, TIME, MESSAGE), ClockLayout.DATE_TIME_MSG.lines)
        assertEquals(listOf(MESSAGE, TIME, DATE), ClockLayout.MSG_TIME_DATE.lines)
        assertEquals(listOf(MESSAGE, TIME), ClockLayout.MSG_ON_TOP.lines)
        assertEquals(listOf(TIME, MESSAGE), ClockLayout.MSG_ON_BOTTOM.lines)
    }

    @Test
    fun `when every layout is drawn, should draw the time exactly once`() {
        ClockLayout.entries.forEach { assertEquals(1, it.lines.count { line -> line == TIME }) }
    }

    @Test
    fun `when a layout has no date line, should not show the date`() {
        val withoutDate = ClockLayout.entries.filterNot { it.showsDate }

        assertEquals(listOf(ClockLayout.TIME_ONLY, ClockLayout.MSG_ON_TOP, ClockLayout.MSG_ON_BOTTOM), withoutDate)
    }

    @Test
    fun `when a layout has a message line, should show the message`() {
        val withMessage = ClockLayout.entries.filter { it.showsMessage }

        assertEquals(
            listOf(
                ClockLayout.DATE_TIME_MSG,
                ClockLayout.MSG_TIME_DATE,
                ClockLayout.MSG_ON_TOP,
                ClockLayout.MSG_ON_BOTTOM
            ),
            withMessage
        )
        assertFalse(ClockLayout.TIME_ONLY.showsMessage)
        assertTrue(ClockLayout.TIME_ON_TOP.showsDate)
    }

    @Test
    fun `when looking a layout up, should find its key and ignore old styles and garbage`() {
        ClockLayout.entries.forEach { assertEquals(it, ClockLayout.fromKey(it.key)) }
        assertNull(ClockLayout.fromKey("CUSTOM_GREETING"))
        assertNull(ClockLayout.fromKey(null))
    }

    @Test
    fun `when reading a message key, should fall back to the daily greeting`() {
        assertEquals(ClockMessage.CUSTOM, ClockMessage.fromKey("CUSTOM"))
        assertEquals(ClockMessage.GREETING, ClockMessage.fromKey("GREETING"))
        assertEquals(ClockMessage.GREETING, ClockMessage.fromKey("NOPE"))
        assertEquals(ClockMessage.GREETING, ClockMessage.fromKey(null))
    }

    @Test
    fun `when a new install has no clock saved, should start with the old default look`() {
        assertEquals(ClockLayout.DATE_ON_TOP, ClockLayout.DEFAULT)
        assertEquals(ClockLayout.DEFAULT.key, ClockConfig().style)
        assertEquals(ClockMessage.DEFAULT.key, ClockConfig().message)
    }
}
