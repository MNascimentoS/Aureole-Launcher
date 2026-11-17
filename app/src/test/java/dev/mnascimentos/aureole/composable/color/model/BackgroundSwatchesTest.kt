package dev.mnascimentos.aureole.composable.color.model

import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.core.designsystem.palette.PaletteCategory
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundSwatchesTest {

    @Test
    fun `theme swatches are the background, surface, variant and outline of the theme`() {
        val colors = frostbitePalette.colors

        val swatches = themeSwatches(colors).map { it.color }

        assertEquals(
            listOf(colors.background, colors.surface, colors.surfaceVariant, colors.outline).map { it.toArgb() },
            swatches
        )
    }

    @Test
    fun `the four categories together cover every palette once`() {
        val total = PaletteCategory.entries.sumOf { paletteSwatches(it).size }

        assertEquals(allPalettes.map { it.name }.distinct().size, total)
        assertTrue(PaletteCategory.entries.all { paletteSwatches(it).isNotEmpty() })
    }

    @Test
    fun `the palette tone defaults to the variant`() {
        val swatch = paletteSwatches(PaletteCategory.COLD).first()
        val palette = allPalettes.first { it.name == swatch.paletteName }

        assertEquals(palette.colors.surfaceVariant.toArgb(), swatch.color)
    }

    @Test
    fun `the tone ramp has seven increasingly light tones of the same hue`() {
        val ramp = toneRamp(200f)
        val lightness = ramp.map { color -> FloatArray(3).also { ColorUtils.colorToHSL(color, it) }[2] }

        assertEquals(7, ramp.size)
        assertEquals(lightness.sorted(), lightness)
        ramp.forEach { assertEquals(200f, hueOf(it), 2f) }
    }

    @Test
    fun `white is a light background and the Frostbite background is not`() {
        assertTrue(isLightBackground(0xFFFFFFFF.toInt()))
        assertFalse(isLightBackground(0xFF12181D.toInt()))
    }

    @Test
    fun `the open category is the one of the current color, cold when it is no palette tone`() {
        val warm = paletteSwatches(PaletteCategory.WARM).first().color

        assertEquals(PaletteCategory.WARM, initialPaletteCategory(warm))
        assertEquals(PaletteCategory.COLD, initialPaletteCategory(0x00123456))
    }
}
