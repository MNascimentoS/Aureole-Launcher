package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ClockSettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.resetClockSettings()
    }

    @Test
    fun `when initialized, should load default clock settings`() {
        assertFalse(repository.isClockBackgroundEnabled)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_STYLE, repository.clockStyle)
        assertEquals("", repository.clockCustomGreeting)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_ALIGNMENT, repository.clockAlignment)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_FONT_FAMILY, repository.clockFontFamily)
        assertTrue(repository.clockFontBold)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_TIME_FORMAT, repository.clockTimeFormat)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_DATE_FORMAT, repository.clockDateFormat)
        assertEquals(0, repository.clockTextColor)
        assertEquals(0, repository.clockBackgroundColor)
    }

    @Test
    fun `when updating clock settings, should persist updated values`() {
        repository.isClockBackgroundEnabled = true
        repository.clockStyle = "MSG_ON_BOTTOM"
        repository.clockCustomGreeting = "Boa tarde"
        repository.clockAlignment = "START"
        repository.clockFontFamily = "SERIF"
        repository.clockFontBold = false
        repository.clockTimeFormat = "24H"
        repository.clockDateFormat = "SHORT"
        repository.clockTextColor = 0xFF90CAF9.toInt()
        repository.clockBackgroundColor = 0xFF1E2124.toInt()

        assertTrue(repository.isClockBackgroundEnabled)
        assertEquals("MSG_ON_BOTTOM", repository.clockStyle)
        assertEquals("Boa tarde", repository.clockCustomGreeting)
        assertEquals("START", repository.clockAlignment)
        assertEquals("SERIF", repository.clockFontFamily)
        assertFalse(repository.clockFontBold)
        assertEquals("24H", repository.clockTimeFormat)
        assertEquals("SHORT", repository.clockDateFormat)
        assertEquals(0xFF90CAF9.toInt(), repository.clockTextColor)
        assertEquals(0xFF1E2124.toInt(), repository.clockBackgroundColor)
    }

    @Test
    fun `when resetting clock settings, should restore default values`() {
        repository.clockStyle = "MSG_TIME_DATE"
        repository.clockTextColor = 0xFFE1BEE7.toInt()
        repository.isClockBackgroundEnabled = true

        repository.resetClockSettings()

        assertFalse(repository.isClockBackgroundEnabled)
        assertEquals(SettingsRepository.DEFAULT_CLOCK_STYLE, repository.clockStyle)
        assertEquals(0, repository.clockTextColor)
    }

    @Test
    fun `when the settings hold an old clock style, should read it as its layout and message`() {
        repository.clockStyle = "CUSTOM_GREETING"

        assertEquals("MSG_TIME_DATE", repository.clockStyle)
        assertEquals("CUSTOM", repository.clockMessage)
        assertEquals("MSG_TIME_DATE", repository.globalClockConfig().style)
        assertEquals("CUSTOM", repository.globalClockConfig().message)
    }

    @Test
    fun `when the settings hold an unknown style, should read the default layout with the greeting`() {
        repository.clockStyle = "STANDARD"

        assertEquals(SettingsRepository.DEFAULT_CLOCK_STYLE, repository.clockStyle)
        assertEquals("GREETING", repository.clockMessage)
    }

    @Test
    fun `when picking a message and resetting, should save it and then drop it`() {
        repository.clockStyle = "MSG_ON_BOTTOM"
        repository.clockMessage = "CUSTOM"
        assertEquals("CUSTOM", repository.clockMessage)

        repository.resetClockSettings()

        assertEquals(SettingsRepository.DEFAULT_CLOCK_STYLE, repository.clockStyle)
        assertEquals("GREETING", repository.clockMessage)
    }
}
