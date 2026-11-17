package dev.mnascimentos.aureole.feature.settings.model

import com.google.android.play.core.install.model.InstallStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsUpdateRingTest {

    @Test
    fun `when downloading with a known size, should show the real fraction`() {
        val ring = settingsUpdateRingFor(InstallStatus.DOWNLOADING, bytesDownloaded = 25L, totalBytes = 100L)

        assertEquals(SettingsUpdateRing.Downloading(0.25f), ring)
        assertEquals(0.25f, ring.ringProgress)
    }

    @Test
    fun `when downloading with an unknown size or pending, should spin`() {
        assertEquals(SettingsUpdateRing.Checking, settingsUpdateRingFor(InstallStatus.DOWNLOADING, 10L, 0L))
        assertEquals(SettingsUpdateRing.Checking, settingsUpdateRingFor(InstallStatus.PENDING, 0L, 0L))
        assertNull(SettingsUpdateRing.Checking.ringProgress)
    }

    @Test
    fun `when nothing is downloading, should hide the ring`() {
        listOf(
            InstallStatus.UNKNOWN,
            InstallStatus.DOWNLOADED,
            InstallStatus.INSTALLED,
            InstallStatus.FAILED,
            InstallStatus.CANCELED
        ).forEach { status ->
            assertEquals(SettingsUpdateRing.Hidden, settingsUpdateRingFor(status, 0L, 0L))
        }
    }

    @Test
    fun `when more bytes than the total arrive, should cap the fraction at full`() {
        assertEquals(SettingsUpdateRing.Downloading(1f), settingsUpdateRingFor(InstallStatus.DOWNLOADING, 120L, 100L))
    }
}
