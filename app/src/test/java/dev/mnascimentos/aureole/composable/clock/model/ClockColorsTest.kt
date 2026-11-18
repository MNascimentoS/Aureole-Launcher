package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClockColorsTest {

    /** Every color the old chips could save, by name, as the ARGB they stored. */
    private val legacyPresets = mapOf(
        "Ice Blue" to 0xFF90CAF9.toInt(),
        "Lavender" to 0xFFE1BEE7.toInt(),
        "White" to 0xFFFFFFFF.toInt(),
        "Gold" to 0xFFFFE082.toInt(),
        "Mint" to 0xFFA5D6A7.toInt(),
        "Coral" to 0xFFFFAB91.toInt(),
        "Dark Slate" to 0xFF1E2124.toInt(),
        "Deep Purple" to 0xFF281C74.toInt(),
        "Night Blue" to 0xFF1A237E.toInt(),
        "Wine" to 0xFF4A148C.toInt(),
        "Dark Green" to 0xFF1B5E20.toInt()
    )

    @Test
    fun `when a clock was saved with a named color, should migrate it to the same fixed color`() {
        legacyPresets.forEach { (name, argb) ->
            assertEquals(name, ThemeColorRef.Fixed(argb), clockColorRefOf(argb, refJson = null))
        }
    }

    @Test
    fun `when the old default or an unknown value was saved, should be the theme default`() {
        assertNull(clockColorRefOf(legacyArgb = 0, refJson = null))
        assertNull(clockColorRefOf(legacyArgb = 0, refJson = "not json"))
        assertNull(clockColorRefOf(legacyArgb = 0, refJson = "{\"role\":\"NOPE\"}"))
    }

    @Test
    fun `when a ref was saved, should win over the old color`() {
        val ref = ThemeColorRef.Role(ThemeColorRole.ACCENT)

        assertEquals(ref, clockColorRefOf(legacyArgb = 0xFF90CAF9.toInt(), refJson = ref.toJson()))
    }

    @Test
    fun `when resolving without a theme, should keep the old ARGB`() {
        assertEquals(0xFF90CAF9.toInt(), resolveClockColor(0xFF90CAF9.toInt(), null, colors = null))
        assertEquals(0, resolveClockColor(0, null, colors = null))
    }
}
