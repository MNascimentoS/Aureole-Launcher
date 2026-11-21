package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class ClockLayoutOptionTest {

    @Test
    fun `when laying out the picker, should show the seven layouts in rows of three`() {
        assertEquals(ClockLayout.entries, ClockLayoutOption.entries.map { it.layout })
        assertEquals(listOf(3, 3, 1), ClockLayoutOption.rows.map { it.size })
        assertEquals(ClockLayoutOption.entries, ClockLayoutOption.rows.flatten())
    }

    @Test
    fun `when an option is picked, should report the key of its layout`() {
        ClockLayoutOption.entries.forEach { assertEquals(it.layout.key, it.key) }
    }

    @Test
    fun `when finding the message segment, should map the stored key and read unknown as the greeting`() {
        assertEquals(0, ClockMessageOption.indexOfKey("GREETING"))
        assertEquals(1, ClockMessageOption.indexOfKey("CUSTOM"))
        assertEquals(0, ClockMessageOption.indexOfKey("NOPE"))
    }

    @Test
    fun `when finding the alignment segment, should map the stored key and return -1 for unknown`() {
        assertEquals(0, ClockAlignmentOption.indexOfKey("START"))
        assertEquals(1, ClockAlignmentOption.indexOfKey("CENTER"))
        assertEquals(2, ClockAlignmentOption.indexOfKey("END"))
        assertEquals(-1, ClockAlignmentOption.indexOfKey("RIGHT"))
    }
}
