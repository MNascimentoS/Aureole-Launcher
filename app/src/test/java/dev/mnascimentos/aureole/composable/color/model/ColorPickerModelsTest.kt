package dev.mnascimentos.aureole.composable.color.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorPickerModelsTest {

    @Test
    fun `there are twelve distinct recommended colors`() {
        assertEquals(12, RecommendedColors.size)
        assertEquals(RecommendedColors.size, RecommendedColors.toSet().size)
    }
}
