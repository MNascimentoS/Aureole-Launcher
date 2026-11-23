package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.theme.model.AureoleFontCatalog
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_MONOSPACED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_ROUNDED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_SERIF
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockFontMigrationTest {

    @Test
    fun `each old font key reads as the catalog font with the same look and bold on`() {
        val expected = mapOf(
            "SANS_SERIF" to FONT_KEY_LAUNCHER,
            "DEFAULT" to FONT_KEY_LAUNCHER,
            "BOLD" to FONT_KEY_LAUNCHER,
            "SERIF" to FONT_KEY_SERIF,
            "MONOSPACE" to FONT_KEY_MONOSPACED,
            "MONO" to FONT_KEY_MONOSPACED,
            "ROUNDED" to FONT_KEY_ROUNDED
        )
        expected.forEach { (old, key) ->
            assertEquals(old, ClockFontChoice(key, isBold = true), resolveClockFont(old, storedBold = false))
        }
    }

    @Test
    fun `old keys are read case-insensitively, as the old picker did`() {
        assertEquals(FONT_KEY_SERIF, resolveClockFont("serif", storedBold = true).fontKey)
        assertEquals(FONT_KEY_MONOSPACED, resolveClockFont("Monospace", storedBold = true).fontKey)
    }

    @Test
    fun `an unknown key reads as the launcher font`() {
        listOf("", "GARBAGE", "Comic Sans").forEach {
            assertEquals(it, FONT_KEY_LAUNCHER, resolveClockFont(it, storedBold = false).fontKey)
        }
    }

    @Test
    fun `a catalog key or the launcher font keeps its key and its bold`() {
        (AureoleFontCatalog.map { it.key } + FONT_KEY_LAUNCHER).forEach { key ->
            assertEquals(ClockFontChoice(key, isBold = false), resolveClockFont(key, storedBold = false))
            assertEquals(ClockFontChoice(key, isBold = true), resolveClockFont(key, storedBold = true))
        }
    }

    @Test
    fun `reading a migrated font again changes nothing`() {
        listOf("SERIF", "BOLD", "ROUNDED", "Montserrat", "nope").forEach { old ->
            val once = resolveClockFont(old, storedBold = false)
            assertEquals(old, once, resolveClockFont(once.fontKey, once.isBold))
        }
    }

    @Test
    fun `withMigratedFont rewrites an old config and keeps a new one`() {
        val old = ClockConfig(fontFamily = "MONOSPACE", isFontBold = false, alignment = "START")

        assertEquals(old.copy(fontFamily = FONT_KEY_MONOSPACED, isFontBold = true), old.withMigratedFont())
        val current = ClockConfig(fontFamily = "Lora", isFontBold = false)
        assertEquals(current, current.withMigratedFont())
    }

    @Test
    fun `the factory clock follows the launcher font in bold, as the old clock looked`() {
        val choice = ClockConfig().fontChoice

        assertTrue(choice.followsLauncher)
        assertTrue(choice.isBold)
        assertFalse(ClockFontChoice(FONT_KEY_SERIF, isBold = true).followsLauncher)
    }
}
