package dev.mnascimentos.aureole.composable.minihome.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.appitem.model.themedIconStyleOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The mini-home draws its apps with the same themed icon rule as the home. */
class MiniHomeIconFillTest {

    private val favorite = MiniHomeFavorite(name = "Phone", color = Color.Green)
    private val themed = themedIconStyleOf(surfaceVariant = Color.Blue, onSurfaceMedium = Color.White)

    @Test
    fun `with themed icons off, the icon keeps its own color and no glyph`() {
        val fill = miniHomeIconFill(favorite, themedIcons = false, themed = themed)
        assertEquals(Color.Green, fill.tile)
        assertNull(fill.glyph)
    }

    @Test
    fun `with themed icons on, the icon takes the themed tile and tint of the home`() {
        val fill = miniHomeIconFill(favorite, themedIcons = true, themed = themed)
        assertEquals(themed.tile, fill.tile)
        assertEquals(Color.White, fill.glyph)
    }

    @Test
    fun `the themed tile is the surface variant, slightly see-through, and the glyph the medium text color`() {
        assertEquals(Color.Blue.copy(alpha = 0.85f), themed.tile)
        assertEquals(Color.White, themed.tint)
    }
}
