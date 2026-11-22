package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import dev.mnascimentos.aureole.core.designsystem.palette.parseThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.toLight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorPickerSheetModelsTest {

    private val oldFixed = ThemeColorRef.Fixed(OLD_ARGB)

    @Test
    fun `each role takes its own tone of a palette`() {
        assertEquals(frostbitePalette.colors.onSurfaceMedium, ComponentColorRole.TEXT.colorIn(frostbitePalette))
        assertEquals(frostbitePalette.colors.surfaceVariant, ComponentColorRole.CARD.colorIn(frostbitePalette))
    }

    @Test
    fun `Theme comes first, then every palette once, without Current when nothing is saved`() {
        val entries = componentColorEntries(selected = null, role = ComponentColorRole.TEXT, isDarkTheme = true)

        assertEquals(ComponentColorEntry.Theme, entries.first())
        val names = entries.drop(1).map { (it as ComponentColorEntry.Palette).name }
        assertEquals(allPalettes.map { it.name }.distinct(), names)
        assertEquals(entries.size, entries.map { it.key }.distinct().size)
    }

    @Test
    fun `an old fixed color that no palette has shows as Current right after Theme, marked`() {
        val entries = componentColorEntries(oldFixed, ComponentColorRole.TEXT, isDarkTheme = true)

        assertEquals(ComponentColorEntry.Current(oldFixed), entries[1])
        assertEquals(entries[1].key, selectedColorEntry(oldFixed, ComponentColorRole.TEXT, isDarkTheme = true).key)
    }

    @Test
    fun `a theme role saved by the old picker is kept as Current`() {
        val role = ThemeColorRef.Role(ThemeColorRole.ACCENT)

        val marked = selectedColorEntry(role, ComponentColorRole.TEXT, isDarkTheme = true)

        assertEquals(ComponentColorEntry.Current(role), marked)
        assertEquals(role, marked.toColorRef(ComponentColorRole.TEXT))
    }

    @Test
    fun `nothing saved marks Theme, which saves null`() {
        val marked = selectedColorEntry(null, ComponentColorRole.CARD, isDarkTheme = true)

        assertEquals(ComponentColorEntry.Theme, marked)
        assertNull(marked.toColorRef(ComponentColorRole.CARD))
    }

    @Test
    fun `picking a palette saves its role tone, which marks that palette again without Current`() {
        val palette = allPalettes[1]
        val entry = componentColorEntries(null, ComponentColorRole.CARD, isDarkTheme = true)
            .first { it is ComponentColorEntry.Palette && it.name == palette.name }

        val saved = entry.toColorRef(ComponentColorRole.CARD)

        assertEquals(ThemeColorRef.Fixed(palette.colors.surfaceVariant.toArgb()), saved)
        val marked = selectedColorEntry(saved, ComponentColorRole.CARD, isDarkTheme = true)
        assertEquals(paletteNameOf(saved, ComponentColorRole.CARD), (marked as ComponentColorEntry.Palette).name)
        val entries = componentColorEntries(saved, ComponentColorRole.CARD, isDarkTheme = true)
        assertTrue(entries.none { it is ComponentColorEntry.Current })
    }

    @Test
    fun `on a light system the tiles show the light variant and save its tone`() {
        val entry = componentColorEntries(null, ComponentColorRole.TEXT, isDarkTheme = false)[1]
            as ComponentColorEntry.Palette

        assertEquals(frostbitePalette.toLight(), entry.palette)
        assertEquals(
            ThemeColorRef.Fixed(frostbitePalette.toLight().colors.onSurfaceMedium.toArgb()),
            entry.toColorRef(ComponentColorRole.TEXT)
        )
    }

    @Test
    fun `a tone saved in the dark variant still marks its palette on a light system`() {
        val saved = ThemeColorRef.Fixed(frostbitePalette.colors.onSurfaceMedium.toArgb())

        val marked = selectedColorEntry(saved, ComponentColorRole.TEXT, isDarkTheme = false)

        assertEquals(frostbitePalette.name, (marked as ComponentColorEntry.Palette).name)
        assertEquals(frostbitePalette.toLight(), marked.palette)
    }

    @Test
    fun `the saved ref survives its JSON round trip`() {
        val saved = componentColorEntries(null, ComponentColorRole.TEXT, isDarkTheme = true)[1]
            .toColorRef(ComponentColorRole.TEXT)

        assertEquals(saved, parseThemeColorRef(saved?.toJson()))
    }

    @Test
    fun `only fixed colors can belong to a palette`() {
        assertNull(paletteNameOf(null, ComponentColorRole.TEXT))
        assertNull(paletteNameOf(ThemeColorRef.Role(ThemeColorRole.SURFACE), ComponentColorRole.CARD))
        assertNull(paletteNameOf(oldFixed, ComponentColorRole.TEXT))
    }

    private companion object {
        const val OLD_ARGB = 0xFF123457.toInt()
    }
}
