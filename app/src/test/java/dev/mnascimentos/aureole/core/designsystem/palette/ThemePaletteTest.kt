package dev.mnascimentos.aureole.core.designsystem.palette

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemePaletteTest {

    @Test
    fun `when getting palette by custom name, should generate palette from seed color`() {
        val seedColor = 0xFF6650A4.toInt()
        val customPalette = getPaletteByName("Custom", isDarkTheme = true, seedColor = seedColor)

        assertEquals("Custom", customPalette.name)
        assertNotNull(customPalette.colors)
        assertNotNull(customPalette.colors.background)
        assertNotNull(customPalette.colors.surface)
    }

    @Test
    fun `when getting palette by custom name in light mode, should return light mode palette`() {
        val seedColor = 0xFF2E7D32.toInt()
        val customLightPalette = getPaletteByName("Custom", isDarkTheme = false, seedColor = seedColor)

        assertEquals("Custom Light", customLightPalette.name)
        assertNotNull(customLightPalette.colors)
    }
}
