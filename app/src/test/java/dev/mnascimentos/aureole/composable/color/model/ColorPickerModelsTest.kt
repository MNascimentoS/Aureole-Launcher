package dev.mnascimentos.aureole.composable.color.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorPickerModelsTest {

    @Test
    fun `formatHex drops the alpha and upper-cases the digits`() {
        assertEquals("#90CAF9", formatHex(0xFF90CAF9.toInt()))
        assertEquals("#80000A", formatHex(0x80000A.toInt()))
        assertEquals("#FFFFFF", formatHex(0x00FFFFFF))
    }

    @Test
    fun `there are twelve recommended colors and the default is one of them`() {
        assertEquals(12, RecommendedColors.size)
        assertEquals(RecommendedColors.size, RecommendedColors.toSet().size)
        assertTrue(DefaultPickerColor in RecommendedColors)
    }

    @Test
    fun `colorForHue gives an opaque color and hueOf reads the hue back`() {
        val color = colorForHue(120f)

        assertEquals(0xFF, color ushr 24)
        assertEquals(120f, hueOf(color), 1f)
    }

    @Test
    fun `colorForHue keeps the hue inside the slider range`() {
        assertEquals(colorForHue(0f), colorForHue(-30f))
        assertEquals(colorForHue(MAX_HUE), colorForHue(MAX_HUE + 40f))
    }

    @Test
    fun `hueOf a gray color is zero`() {
        assertEquals(0f, hueOf(0xFF808080.toInt()), 0f)
    }
}
