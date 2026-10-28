package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ThemeSeedColorTest {

    @Test
    fun `when a palette is named, should seed with its surface variant`() {
        assertEquals(frostbitePalette.colors.surfaceVariant.toArgb(), resolveThemeSeedColor(frostbitePalette.name))
    }

    @Test
    fun `when two palettes differ, should resolve different seeds`() {
        assertNotEquals(resolveThemeSeedColor(frostbitePalette.name), resolveThemeSeedColor("Abyss"))
    }
}
