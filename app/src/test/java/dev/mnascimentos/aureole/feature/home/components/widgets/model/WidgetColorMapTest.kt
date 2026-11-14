package dev.mnascimentos.aureole.feature.home.components.widgets.model

import android.os.Build
import androidx.annotation.RequiresApi
import org.junit.Assert.assertEquals
import org.junit.Test

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class WidgetColorMapTest {
    private val palette = WidgetPalette(
        accent1 = 0xFF3366CC.toInt(),
        accent2 = 0xFF33CC66.toInt(),
        accent3 = 0xFFCC3366.toInt(),
        neutral1 = 0xFF202020.toInt(),
        neutral2 = 0xFF404040.toInt()
    )

    @Test
    fun `should map shade 500 to the palette color itself`() {
        assertEquals(palette.accent1, toneOf(palette.accent1, 500))
    }

    @Test
    fun `should map shade 0 to white and 1000 to black`() {
        assertEquals(0xFFFFFFFF.toInt(), toneOf(palette.accent1, 0))
        assertEquals(0xFF000000.toInt(), toneOf(palette.accent1, 1000))
    }

    @Test
    fun `should lighten below 500 and darken above it`() {
        val light = toneOf(palette.accent1, 200) and 0xFF
        val base = palette.accent1 and 0xFF
        val dark = toneOf(palette.accent1, 800) and 0xFF
        assert(light > base && dark < base)
    }

    @Test
    fun `should cover all five families with thirteen shades each`() {
        val map = buildWidgetColorMap(palette)
        assertEquals(5 * 13, map.size)
        assertEquals(palette.neutral2, map[android.R.color.system_neutral2_500])
    }
}
