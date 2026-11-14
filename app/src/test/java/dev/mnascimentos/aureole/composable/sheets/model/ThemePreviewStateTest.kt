package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.toLight
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.theme.IstokWebFontFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePreviewStateTest {

    private val base = AureoleThemeConfig(darkTheme = true)
    private val other = allPalettes.first { it.colors != base.aureoleColors }

    @Test
    fun `nothing pending resolves to the base config`() {
        assertEquals(base, resolvePreviewConfig(base, ThemePreviewState()))
    }

    @Test
    fun `pending palette replaces only the colors`() {
        val resolved = resolvePreviewConfig(base, ThemePreviewState(palette = other))

        assertEquals(other.colors, resolved.aureoleColors)
        assertEquals(base.aureoleFontName, resolved.aureoleFontName)
    }

    @Test
    fun `pending font replaces only the font`() {
        val resolved = resolvePreviewConfig(base, ThemePreviewState(fontName = "Roboto"))

        assertEquals("Roboto", resolved.aureoleFontName)
        assertEquals(base.aureoleColors, resolved.aureoleColors)
    }

    @Test
    fun `isDirty is false with nothing pending and true once something differs`() {
        assertFalse(isDirty(base, ThemePreviewState()))
        assertTrue(isDirty(base, ThemePreviewState(fontName = "Roboto")))
        assertTrue(isDirty(base, ThemePreviewState(palette = other)))
    }

    @Test
    fun `choosing what is already applied is not dirty`() {
        assertFalse(isDirty(base, ThemePreviewState(fontName = base.aureoleFontName)))
    }

    @Test
    fun `discard goes back to the initial state`() {
        val pending = ThemePreviewState(palette = other, fontName = "Roboto")

        val discarded = pending.discard()

        assertNull(discarded.palette)
        assertNull(discarded.fontName)
        assertEquals(base, resolvePreviewConfig(base, discarded))
    }

    @Test
    fun `base palette name drops the light suffix`() {
        assertEquals("Frostbite", "Frostbite Light".basePaletteName())
        assertEquals("Frostbite", "Frostbite".basePaletteName())
    }

    @Test
    fun `findBasePalette finds dark and light names and rejects unknown ones`() {
        val palette = allPalettes.first()

        assertEquals(palette, findBasePalette(palette.name))
        assertEquals(palette, findBasePalette(palette.toLight().name))
        assertNull(findBasePalette("Nope"))
    }

    @Test
    fun `fontNameOf maps a family to its name and falls back to the default`() {
        assertEquals("Roboto", fontNameOf(AvailableFonts.getValue("Roboto")))
        assertEquals("Istok Web", fontNameOf(IstokWebFontFamily))
        assertEquals("Istok Web", fontNameOf(androidx.compose.ui.text.font.FontFamily.Cursive))
    }

    @Test
    fun `applying with no tap should save the palette already shown as selected`() {
        assertEquals("Frostbite", resolveAppliedPalette(pendingName = null, selectedName = "Frostbite")?.name)
    }

    @Test
    fun `applying with a tap should save the tapped palette`() {
        val tapped = other.name

        assertEquals(tapped, resolveAppliedPalette(pendingName = tapped, selectedName = "Frostbite")?.name)
    }

    @Test
    fun `applying with no tap should save the font already shown as selected`() {
        assertEquals("Istok Web", resolveAppliedFont(pendingName = null, selectedName = "Istok Web"))
    }

    @Test
    fun `applying with a tap should save the tapped font`() {
        assertEquals("Roboto", resolveAppliedFont(pendingName = "Roboto", selectedName = "Istok Web"))
    }
}
