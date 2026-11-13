package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardsGlassModelsTest {

    @Test
    fun `initialCardsGlassDraft should show no glass when the blur is off`() {
        val draft = initialCardsGlassDraft(20, isHazeEnabled = false, hazeOpacity = 0.5f, storedLevel = GlassLevel.LOW)
        assertEquals(GlassLevel.NONE, draft.glass)
        assertEquals(20, draft.radiusDp)
    }

    @Test
    fun `initialCardsGlassDraft should use the stored level when the blur is on`() {
        val draft = initialCardsGlassDraft(16, isHazeEnabled = true, hazeOpacity = 0.7f, storedLevel = GlassLevel.LOW)
        assertEquals(CardsGlassDraft(16, GlassLevel.LOW, 0.7f), draft)
    }

    @Test
    fun `blurOpacityOf should match a chip within tolerance and ignore values between chips`() {
        assertEquals(BlurOpacity.LOW, blurOpacityOf(0.2f))
        assertEquals(BlurOpacity.SOLID, blurOpacityOf(0.895f))
        assertNull(blurOpacityOf(0.35f))
    }

    @Test
    fun `isHazeEnabled should be false only for no glass`() {
        assertFalse(CardsGlassDraft(16, GlassLevel.NONE, 0.5f).isHazeEnabled())
        assertTrue(CardsGlassDraft(16, GlassLevel.LOW, 0.5f).isHazeEnabled())
    }

    @Test
    fun `withCardsGlass should apply radius, opacity and level`() {
        val state = SettingsUiState().withCardsGlass(CardsGlassDraft(8, GlassLevel.MAX, 0.9f))
        assertEquals(8, state.cornerRadiusDp)
        assertEquals(0.9f, state.hazeOpacity)
        assertEquals(GlassLevel.MAX, state.hazeBlurLevel)
        assertTrue(state.isHazeEnabled)
    }

    @Test
    fun `withCardsGlass should keep the last level when the blur is switched off`() {
        val before = SettingsUiState(hazeBlurLevel = GlassLevel.LOW, isHazeEnabled = true)
        val state = before.withCardsGlass(CardsGlassDraft(16, GlassLevel.NONE, 0.5f))
        assertFalse(state.isHazeEnabled)
        assertEquals(GlassLevel.LOW, state.hazeBlurLevel)
    }

    @Test
    fun `toCardsGlassDraft and shownGlassLevel should reflect the settings state`() {
        val on = SettingsUiState(isHazeEnabled = true, hazeBlurLevel = GlassLevel.MAX, cornerRadiusDp = 12)
        assertEquals(GlassLevel.MAX, on.shownGlassLevel())
        assertEquals(12, on.toCardsGlassDraft().radiusDp)
        assertEquals(GlassLevel.NONE, on.copy(isHazeEnabled = false).shownGlassLevel())
    }

    @Test
    fun `options should list the corner radii and the four opacities in order`() {
        assertEquals(listOf(0, 8, 12, 16, 24), CardRadiusOptions)
        assertEquals(listOf(0.2f, 0.5f, 0.7f, 0.9f), BlurOpacity.entries.map { it.value })
    }
}
