package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tiles of the clock's color page: "None" first for the card, "Theme" for the theme card (RF-E2, RF-E4). */
class ClockColorTilesTest {

    private val theme = ColorPageTile.Entry(ComponentColorEntry.Theme)

    @Test
    fun `when the card has no color, should mark None, first of the card tiles`() {
        val tiles = clockColorTiles(saved = null, role = ComponentColorRole.CARD, isDarkTheme = true)

        assertEquals(ColorPageTile.None, tiles.first())
        assertEquals(theme, tiles[1])
        assertEquals(ColorPageTile.None, selectedClockColorTile(null, ComponentColorRole.CARD, isDarkTheme = true))
    }

    @Test
    fun `when the card is the theme card, should mark Theme without a Current tile`() {
        val tiles = clockColorTiles(CLOCK_CARD_THEME_REF, ComponentColorRole.CARD, isDarkTheme = true)

        assertEquals(theme, selectedClockColorTile(CLOCK_CARD_THEME_REF, ComponentColorRole.CARD, isDarkTheme = true))
        assertTrue(tiles.none { it is ColorPageTile.Entry && it.entry is ComponentColorEntry.Current })
        assertEquals(1 + 1 + allPalettes.distinctBy { it.name }.size, tiles.size)
    }

    @Test
    fun `when the text has no color, should mark Theme and offer no None`() {
        val tiles = clockColorTiles(saved = null, role = ComponentColorRole.TEXT, isDarkTheme = false)

        assertEquals(theme, tiles.first())
        assertTrue(ColorPageTile.None !in tiles)
        assertEquals(theme, selectedClockColorTile(null, ComponentColorRole.TEXT, isDarkTheme = false))
    }

    @Test
    fun `when a tile is picked, should save None and text Theme as null and the card Theme as the theme card`() {
        assertNull(ColorPageTile.None.toClockColorRef(ComponentColorRole.CARD))
        assertNull(theme.toClockColorRef(ComponentColorRole.TEXT))
        assertEquals(CLOCK_CARD_THEME_REF, theme.toClockColorRef(ComponentColorRole.CARD))
    }

    @Test
    fun `when the card color is another theme role, should show it as Current`() {
        val other = ThemeColorRef.Role(ThemeColorRole.SURFACE)

        val tile = selectedClockColorTile(other, ComponentColorRole.CARD, isDarkTheme = true)

        assertEquals(ColorPageTile.Entry(ComponentColorEntry.Current(other)), tile)
    }
}
