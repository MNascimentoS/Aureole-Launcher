package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchBarBackgroundColorTest {

    private val surface = Color(0xFF202020)

    @Test
    fun `without glass the background is the surface color`() {
        assertEquals(surface, searchBarBackgroundColor(AureoleSearchBarHazeConfig(isEnabled = false), surface))
        assertEquals(surface, searchBarBackgroundColor(AureoleSearchBarHazeConfig(isEnabled = true), surface))
    }

    @Test
    fun `with glass the alpha follows the opacity within the bubble limits`() {
        val state = HazeState()
        val mid = searchBarBackgroundColor(AureoleSearchBarHazeConfig(state, isEnabled = true, opacity = 0.5f), surface)
        assertEquals(0.425f, mid.alpha, 0.01f)
        val low = searchBarBackgroundColor(AureoleSearchBarHazeConfig(state, isEnabled = true, opacity = 0f), surface)
        assertEquals(0.3f, low.alpha, 0.01f)
        val high = searchBarBackgroundColor(AureoleSearchBarHazeConfig(state, isEnabled = true, opacity = 1f), surface)
        assertEquals(0.85f, high.alpha, 0.01f)
    }
}
