package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.frostbiteColors
import org.junit.Assert.assertEquals
import org.junit.Test

class PressIndicationTest {

    @Test
    fun `the press highlight is the high emphasis color so it follows the theme`() {
        val dark = frostbiteColors.copy(onSurfaceHigh = Color.White)
        val light = frostbiteColors.copy(onSurfaceHigh = Color.Black)

        assertEquals(Color.White, pressHighlightColor(dark))
        assertEquals(Color.Black, pressHighlightColor(light))
    }
}
