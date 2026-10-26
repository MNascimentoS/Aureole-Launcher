package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryMethodsTest {

    @Test
    fun testSettingsRepositoryGettersAndSetters() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.resetClockSettings()
        repository.clearCustomWallpaper()
        repository.restoreDefaultWallpaper()

        assertNotNull(repository.clockStyle)
        assertNotNull(repository.clockAlignment)
        assertNotNull(repository.clockFontFamily)
        assertNotNull(repository.clockTimeFormat)
        assertNotNull(repository.clockDateFormat)
        assertNotNull(repository.containerPosition)
        assertNotNull(repository.selectedThemeName)
        assertNotNull(repository.selectedFontName)
        assertNotNull(repository.settingsButtonPosition)
        assertNotNull(repository.searchIconPosition)
        assertNotNull(repository.wallpaperScaleType)

        repository.checkDefaultLauncher()
    }
}
