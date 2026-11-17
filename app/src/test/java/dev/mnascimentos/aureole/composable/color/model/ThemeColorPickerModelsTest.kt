package dev.mnascimentos.aureole.composable.color.model

import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeColorPickerModelsTest {

    private val colors = frostbitePalette.colors

    @Test
    fun `theme swatches are the five roles of the theme in order`() {
        val swatches = themeRoleSwatches(colors)

        assertEquals(
            listOf(
                R.string.settings_background_swatch_background,
                R.string.settings_background_swatch_surface,
                R.string.settings_background_swatch_variant,
                R.string.settings_background_swatch_outline,
                R.string.settings_background_swatch_accent
            ),
            swatches.map { it.labelRes }
        )
        assertEquals(
            listOf(colors.background, colors.surface, colors.surfaceVariant, colors.outline, colors.onSurfaceMedium)
                .map { it.toArgb() },
            swatches.map { it.color }
        )
    }

    @Test
    fun `each swatch maps back to its role`() {
        assertEquals(ThemeColorRole.entries, themeRoleSwatches(colors).map { themeRoleOf(it) })
        assertNull(themeRoleOf(ThemeSwatch(R.string.settings_background_section_tones, 0)))
    }

    @Test
    fun `only the swatch of the selected role is marked`() {
        val swatches = themeRoleSwatches(colors)
        val selected = ThemeColorRef.Role(ThemeColorRole.OUTLINE)

        assertEquals(listOf(false, false, false, true, false), swatches.map { isThemeRoleSelected(it, selected) })
    }

    @Test
    fun `no theme swatch is marked for a fixed color or for nothing`() {
        val swatches = themeRoleSwatches(colors)

        assertTrue(swatches.none { isThemeRoleSelected(it, ThemeColorRef.Fixed(swatches.first().color)) })
        assertFalse(swatches.any { isThemeRoleSelected(it, null) })
    }

    @Test
    fun `the palette row only gets a fixed color`() {
        assertEquals(0x11223344, fixedPaletteColor(ThemeColorRef.Fixed(0x11223344)))
        assertNull(fixedPaletteColor(ThemeColorRef.Role(ThemeColorRole.SURFACE)))
        assertNull(fixedPaletteColor(null))
    }
}
