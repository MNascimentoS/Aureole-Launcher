package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.utils.GlassBaseBlurRadius
import dev.mnascimentos.aureole.core.designsystem.utils.blurRadius
import org.junit.Assert.assertEquals
import org.junit.Test

class GlassLevelTest {

    @Test
    fun `glassLevelOf should find a level by name and fall back to medium`() {
        assertEquals(GlassLevel.MAX, glassLevelOf("MAX"))
        assertEquals(GlassLevel.LOW, glassLevelOf("LOW"))
        assertEquals(GlassLevel.NONE, glassLevelOf("NONE"))
        assertEquals(GlassLevel.MEDIUM, glassLevelOf("unknown"))
        assertEquals(GlassLevel.MEDIUM, glassLevelOf(""))
    }

    @Test
    fun `blurRadius should keep the base radius at medium and scale it at the other levels`() {
        val base = 24.dp
        assertEquals(base, GlassLevel.MEDIUM.blurRadius(base))
        assertEquals(0.dp, GlassLevel.NONE.blurRadius(base))
        assertEquals(true, GlassLevel.LOW.blurRadius(base) < base)
        assertEquals(true, GlassLevel.MAX.blurRadius(base) > base)
    }

    @Test
    fun `blurRadius should grow with the level for the preview base radius`() {
        val radii = listOf(GlassLevel.LOW, GlassLevel.MEDIUM, GlassLevel.MAX).map { it.blurRadius(GlassBaseBlurRadius) }
        assertEquals(radii.sorted(), radii)
        assertEquals(GlassBaseBlurRadius, radii[1])
    }
}
