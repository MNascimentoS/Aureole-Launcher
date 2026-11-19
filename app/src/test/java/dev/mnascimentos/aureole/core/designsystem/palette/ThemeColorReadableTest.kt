package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeColorReadableTest {

    private val dark = AureoleColors(
        onSurfaceHigh = Color(0xFFF0F0F0),
        onSurfaceMedium = Color(0xFF80CBC4),
        onSurfaceLow = Color(0xFF607D8B),
        outline = Color(0xFF333344),
        surfaceVariant = Color(0xFF222233),
        surface = Color(0xFF111122),
        background = Color(0xFF050510)
    )

    @Test
    fun `a dark color gets the light text of the theme`() {
        assertEquals(dark.onSurfaceHigh, readableOn(Color(0xFF222233), dark))
    }

    @Test
    fun `a pale color gets the dark background of the theme`() {
        assertEquals(dark.background, readableOn(Color(0xFFF5F5DC), dark))
    }

    @Test
    fun `a translucent color is judged as opaque`() {
        assertEquals(dark.background, readableOn(Color(0x80FFFFFF), dark))
    }
}
