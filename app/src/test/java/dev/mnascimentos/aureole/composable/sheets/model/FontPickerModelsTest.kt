package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.designsystem.theme.model.AureoleFontCatalog
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_SERIF
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FontPickerModelsTest {

    private val catalog = AureoleFontCatalog.map { it.key }

    @Test
    fun `the picker of a component lists the launcher font first, then the whole catalog`() {
        val keys = fontPickerKeys(withLauncherFont = true)

        assertEquals(FONT_KEY_LAUNCHER, keys.first())
        assertEquals(catalog, keys.drop(1))
    }

    @Test
    fun `the Customize picker lists only the catalog, without the launcher font`() {
        val keys = fontPickerKeys(withLauncherFont = false)

        assertEquals(catalog, keys)
        assertFalse(FONT_KEY_LAUNCHER in keys)
    }

    @Test
    fun `the current font is always marked`() {
        val component = fontPickerKeys(withLauncherFont = true)
        val launcher = fontPickerKeys(withLauncherFont = false)

        assertEquals(FONT_KEY_SERIF, markedFontKey(FONT_KEY_SERIF, component))
        assertEquals(FONT_KEY_LAUNCHER, markedFontKey(FONT_KEY_LAUNCHER, component))
        assertEquals(FONT_KEY_LAUNCHER, markedFontKey("Lost font", component))
        assertEquals("Istok Web", markedFontKey("Lost font", launcher))
        assertEquals("Lora", markedFontKey("Lora", launcher))
    }
}
