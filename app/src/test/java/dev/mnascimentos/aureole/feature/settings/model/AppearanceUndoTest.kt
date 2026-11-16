package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppearanceUndoTest {

    private val state = SettingsUiState(
        selectedThemeName = "Frostbite",
        manualSeedColor = 5,
        isCustomWallpaperSet = true,
        customWallpaperPath = "/files/custom.jpg"
    )

    @Test
    fun `the snapshot holds the palette, the color and the custom image path`() {
        assertEquals(SeedSnapshot("Frostbite", 5, "/files/custom.jpg"), state.toSeedSnapshot())
    }

    @Test
    fun `without a custom image the snapshot has no path`() {
        assertNull(state.copy(isCustomWallpaperSet = false).toSeedSnapshot().customWallpaperPath)
    }

    @Test
    fun `restoring brings back palette, color and image`() {
        val changed = state.copy(selectedThemeName = "Custom", manualSeedColor = 9, isCustomWallpaperSet = false)

        val restored = changed.withSeedSnapshot(state.toSeedSnapshot(), keepWallpaper = true)

        assertEquals(state, restored)
    }

    @Test
    fun `restoring without the image leaves the background a solid color`() {
        val restored = state.withSeedSnapshot(state.toSeedSnapshot(), keepWallpaper = false)

        assertFalse(restored.isCustomWallpaperSet)
        assertNull(restored.customWallpaperPath)
        assertTrue(restored.manualSeedColor == 5)
    }
}
