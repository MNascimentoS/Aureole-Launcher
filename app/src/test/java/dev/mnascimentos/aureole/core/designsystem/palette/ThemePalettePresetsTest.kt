package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemePalettePresetsTest {

    @Test
    fun `presets keep the hundred palettes with unique names`() {
        assertEquals(100, presetPalettes.size)
        assertEquals(100, presetPalettes.map { it.name }.toSet().size)
    }

    @Test
    fun `presetPalette reads seven opaque RGB values in field order`() {
        val palette = presetPalette("Test", PaletteCategory.COLD, "010203 040506 070809 0A0B0C 0D0E0F 101112 131415")
        assertEquals(Color(0xFF010203), palette.colors.onSurfaceHigh)
        assertEquals(Color(0xFF040506), palette.colors.onSurfaceMedium)
        assertEquals(Color(0xFF070809), palette.colors.onSurfaceLow)
        assertEquals(Color(0xFF0A0B0C), palette.colors.outline)
        assertEquals(Color(0xFF0D0E0F), palette.colors.surfaceVariant)
        assertEquals(Color(0xFF101112), palette.colors.surface)
        assertEquals(Color(0xFF131415), palette.colors.background)
    }

    @Test
    fun `first and last presets keep their original colors`() {
        val abyss = presetPalettes.first()
        assertEquals("Abyss", abyss.name)
        assertEquals(PaletteCategory.COLD, abyss.category)
        assertEquals(Color(0xFFE0F7FA), abyss.colors.onSurfaceHigh)
        assertEquals(Color(0xFF001F20), abyss.colors.background)
        val last = presetPalettes.last()
        assertEquals("Mahogany", last.name)
        assertEquals(PaletteCategory.WARM, last.category)
        assertEquals(Color(0xFFE0C8B8), last.colors.onSurfaceHigh)
        assertEquals(Color(0xFF140808), last.colors.background)
    }
}
