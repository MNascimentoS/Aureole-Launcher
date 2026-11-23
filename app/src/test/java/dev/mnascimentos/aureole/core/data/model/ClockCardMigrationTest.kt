package dev.mnascimentos.aureole.core.data.model

import com.google.gson.Gson
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The removed card switch becomes the card color, keeping the look every clock had (RF-E3). */
class ClockCardMigrationTest {

    private val gson = Gson()
    private val savedRef = ThemeColorRef.Fixed(0xFF1A237E.toInt()).toJson()

    @Test
    fun `when the card was off, should have no card color`() {
        assertNull(migratedClockCardRef(isEnabled = false, legacyArgb = 0, refJson = savedRef))
    }

    @Test
    fun `when the card was on with no color, should take the theme card`() {
        val migrated = migratedClockCardRef(isEnabled = true, legacyArgb = 0, refJson = null)

        assertEquals(CLOCK_CARD_THEME_REF.toJson(), migrated)
    }

    @Test
    fun `when the card was on with a color, should keep it`() {
        assertEquals(savedRef, migratedClockCardRef(isEnabled = true, legacyArgb = 0, refJson = savedRef))
        assertEquals(
            ThemeColorRef.Fixed(OLD_ARGB).toJson(),
            migratedClockCardRef(isEnabled = true, legacyArgb = OLD_ARGB, refJson = null)
        )
    }

    @Test
    fun `when an old JSON is read, should fold the switch into the card color`() {
        val offJson = """{"isBackgroundEnabled":false,"backgroundColor":$OLD_ARGB}"""
        val off = gson.fromJson(offJson, ClockConfig::class.java)
        val on = gson.fromJson("""{"isBackgroundEnabled":true}""", ClockConfig::class.java)

        assertFalse(off.withMigratedCard().isBackgroundEnabled)
        assertEquals(0, off.withMigratedCard().backgroundColor)
        assertEquals(CLOCK_CARD_THEME_REF.toJson(), on.withMigratedCard().backgroundColorRef)
        assertTrue(on.withMigratedCard().isBackgroundEnabled)
    }

    @Test
    fun `when a config is saved, should not write the old switch`() {
        val json = gson.toJson(ClockConfig(backgroundColorRef = savedRef))

        assertFalse(json.contains("isBackgroundEnabled"))
        val read = gson.fromJson(json, ClockConfig::class.java)
        assertEquals(read, read.withMigratedCard())
        assertTrue(read.isBackgroundEnabled)
    }

    @Test
    fun `when a clock is new, should start with no card`() {
        assertFalse(ClockConfig().isBackgroundEnabled)
    }

    private companion object {
        const val OLD_ARGB = 0xFF123457.toInt()
    }
}
