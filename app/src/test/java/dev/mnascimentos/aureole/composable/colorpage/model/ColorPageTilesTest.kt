package dev.mnascimentos.aureole.composable.colorpage.model

import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The tiles of a card's color page: "None" first, "Theme" for the component's own theme color. */
class ColorPageTilesTest {

    private val themeRef = ThemeColorRef.Role(ThemeColorRole.SURFACE)
    private val theme = ColorPageTile.Entry(ComponentColorEntry.Theme)

    @Test
    fun `None should come first and stand for no color`() {
        val tiles = cardColorTiles(saved = null, themeRef = themeRef, isDarkTheme = true)

        assertEquals(ColorPageTile.None, tiles.first())
        assertEquals(theme, tiles[1])
        assertEquals(ColorPageTile.None, selectedCardColorTile(null, themeRef, isDarkTheme = true))
        assertNull(ColorPageTile.None.toCardColorRef(themeRef))
    }

    @Test
    fun `the theme color should be Theme, not a Current color`() {
        val tiles = cardColorTiles(saved = themeRef, themeRef = themeRef, isDarkTheme = false)

        assertEquals(theme, selectedCardColorTile(themeRef, themeRef, isDarkTheme = false))
        assertTrue(tiles.none { it is ColorPageTile.Entry && it.entry is ComponentColorEntry.Current })
        assertEquals(themeRef, theme.toCardColorRef(themeRef))
    }

    @Test
    fun `a color in no palette should show as Current`() {
        val other = ThemeColorRef.Fixed(0x12345678)

        val tile = selectedCardColorTile(other, themeRef, isDarkTheme = true)

        assertEquals(ColorPageTile.Entry(ComponentColorEntry.Current(other)), tile)
        assertEquals(other, tile.toCardColorRef(themeRef))
    }
}
