package dev.mnascimentos.aureole.core.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockLayoutMigrationTest {

    private fun choice(layout: ClockLayout, message: ClockMessage = ClockMessage.GREETING) =
        ClockLayoutChoice(layout, message)

    @Test
    fun `when the old style is Date on top, should keep the date over the time`() {
        assertEquals(choice(ClockLayout.DATE_ON_TOP), resolveClockLayout("DATE_ON_TOP", null))
    }

    @Test
    fun `when the old style is Time only, should keep only the time`() {
        assertEquals(choice(ClockLayout.TIME_ONLY), resolveClockLayout("TIME_ONLY", null))
    }

    @Test
    fun `when the old style is Info and date, should draw the greeting, the time and the date`() {
        assertEquals(choice(ClockLayout.MSG_TIME_DATE), resolveClockLayout("GREETING_AND_DATE", null))
    }

    @Test
    fun `when the old style is Daily greeting, should keep its greeting, time and date (D-19)`() {
        assertEquals(choice(ClockLayout.MSG_TIME_DATE), resolveClockLayout("DYNAMIC_GREETING", null))
    }

    @Test
    fun `when the old style is Custom text, should keep the layout and show the custom text (D-19)`() {
        assertEquals(
            choice(ClockLayout.MSG_TIME_DATE, ClockMessage.CUSTOM),
            resolveClockLayout("CUSTOM_GREETING", "GREETING")
        )
    }

    @Test
    fun `when the style is unknown, should fall back to the default layout`() {
        assertEquals(choice(ClockLayout.DEFAULT), resolveClockLayout("STANDARD", null))
        assertEquals(choice(ClockLayout.DEFAULT), resolveClockLayout(null, null))
    }

    @Test
    fun `when the style is already a layout, should keep it and its message`() {
        ClockLayout.entries.forEach { layout ->
            assertEquals(choice(layout, ClockMessage.CUSTOM), resolveClockLayout(layout.key, "CUSTOM"))
        }
    }

    @Test
    fun `when migrating a config, should keep the custom text and every other field`() {
        val old = ClockConfig(style = "CUSTOM_GREETING", customGreeting = "Oi", alignment = "END", fontFamily = "SERIF")

        val migrated = old.withMigratedLayout()

        assertEquals(old.copy(style = "MSG_TIME_DATE", message = "CUSTOM"), migrated)
        assertEquals(migrated, migrated.withMigratedLayout())
    }
}
