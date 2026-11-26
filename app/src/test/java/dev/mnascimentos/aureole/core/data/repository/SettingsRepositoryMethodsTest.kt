package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

    @Test
    fun `swipe down for notifications should be off by default and keep what is saved`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)
        repository.prefs.edit().remove("swipe_down_notifications").commit()

        assertFalse(repository.isSwipeDownNotificationsEnabled)
        repository.prefs.edit().putBoolean("swipe_down_notifications", true).commit()
        assertTrue(SettingsRepository(context).isSwipeDownNotificationsEnabled)

        repository.prefs.edit().remove("swipe_down_notifications").commit()
    }

    @Test
    fun `swipe up for all apps should be off by default and keep what is saved`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)
        repository.prefs.edit().remove("swipe_up_all_apps").commit()

        assertFalse(repository.isSwipeUpAllAppsEnabled)
        repository.prefs.edit().putBoolean("swipe_up_all_apps", true).commit()
        assertTrue(SettingsRepository(context).isSwipeUpAllAppsEnabled)

        repository.prefs.edit().remove("swipe_up_all_apps").commit()
    }
}
