package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsSummariesTest {

    @Test
    fun `paletteSummary returns the trimmed name or null when blank`() {
        assertEquals("Frostbite", paletteSummary(" Frostbite "))
        assertNull(paletteSummary("  "))
        assertNull(paletteSummary(""))
    }

    @Test
    fun `fontSummary returns the trimmed name or null when blank`() {
        assertEquals("Istok Web", fontSummary("Istok Web"))
        assertNull(fontSummary(" "))
    }

    @Test
    fun `backgroundSummary tells custom image from solid color`() {
        assertEquals(R.string.settings_appearance_custom_image, backgroundSummary(true))
        assertEquals(R.string.settings_appearance_solid_color, backgroundSummary(false))
    }

    @Test
    fun `glassSummary follows the level the sheet would open on`() {
        val state = SettingsUiState(hazeBlurLevel = GlassLevel.MEDIUM, isHazeEnabled = true)
        assertEquals(state.shownGlassLevel().labelRes, glassSummary(state))
        val off = SettingsUiState(isHazeEnabled = false)
        assertEquals(off.shownGlassLevel().labelRes, glassSummary(off))
    }

    @Test
    fun `nextWallpaperScaleType cycles Crop Fit Fill`() {
        assertEquals("Fit", nextWallpaperScaleType("Crop"))
        assertEquals("Fill", nextWallpaperScaleType("Fit"))
        assertEquals("Crop", nextWallpaperScaleType("Fill"))
        assertEquals("Crop", nextWallpaperScaleType("anything"))
    }
}
