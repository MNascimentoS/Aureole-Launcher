package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val OTHER_COLOR = 0xFF112233.toInt()

/** The one-time migration of a background saved from the removed "Vivid" swatches (RF-03 of polimento-ui). */
@RunWith(RobolectricTestRunner::class)
class VividBackgroundRepositoryMigrationTest {

    private lateinit var settings: SettingsRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("aureole_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        settings = SettingsRepository(context)
    }

    @Test
    fun `a Vivid background becomes the default palette, once`() {
        settings.selectedThemeName = "Custom"
        settings.manualSeedColor = RecommendedColors[3]

        settings.migrateVividBackground(RecommendedColors)

        assertEquals(frostbitePalette.name, settings.selectedThemeName)
        assertEquals(resolveThemeSeedColor(frostbitePalette.name), settings.manualSeedColor)

        // A Vivid color saved after the migration (it can no longer be picked) is left alone.
        settings.selectedThemeName = "Custom"
        settings.manualSeedColor = RecommendedColors[3]
        settings.migrateVividBackground(RecommendedColors)
        assertEquals("Custom", settings.selectedThemeName)
    }

    @Test
    fun `any other background stays as it is`() {
        settings.selectedThemeName = "Custom"
        settings.manualSeedColor = OTHER_COLOR

        settings.migrateVividBackground(RecommendedColors)

        assertEquals("Custom", settings.selectedThemeName)
        assertEquals(OTHER_COLOR, settings.manualSeedColor)
    }
}
