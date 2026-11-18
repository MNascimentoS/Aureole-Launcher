package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import android.content.Intent
import android.provider.AlarmClock
import android.provider.Settings
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SystemShortcutsTest {

    private val targets = listOf(
        "Browser", "Calendar", "Calculator", "Camera", "Clock", "Contacts", "Dialer", "Email", "Search", "Settings",
        "Files", "Messages", "Music Player", "Battery Settings", "Bluetooth Settings", "Device Settings",
        "Display Settings", "Location Settings", "Sound Settings", "Wi-Fi Settings"
    )

    @Test
    fun `every target has its own intent and a name`() {
        targets.forEach { target ->
            assertNotNull(target, systemIntentFor(target))
            assertNotNull(target, ShortcutAction.systemIntent(target).nameRes())
        }
    }

    @Test
    fun `an unknown target gives no intent, never Settings`() {
        assertNull(systemIntentFor("Nothing"))
        assertNull(systemIntentFor(""))
    }

    @Test
    fun `targets use their specific action or category`() {
        assertEquals(Intent.CATEGORY_APP_CALENDAR, systemIntentFor("Calendar")?.selector?.categories?.single())
        assertEquals(Intent.CATEGORY_APP_BROWSER, systemIntentFor("Browser")?.selector?.categories?.single())
        assertEquals(AlarmClock.ACTION_SHOW_ALARMS, systemIntentFor("Clock")?.action)
        assertEquals(Settings.ACTION_WIFI_SETTINGS, systemIntentFor("Wi-Fi Settings")?.action)
        assertEquals(Settings.ACTION_SETTINGS, systemIntentFor("Settings")?.action)
        assertEquals(Intent.ACTION_DIAL, systemIntentFor("Dialer")?.action)
    }

    private fun environment(resolved: Set<String>) = ShortcutEnvironment(
        apps = emptyList(),
        nameOf = { it.target },
        unavailableLabel = { "$it (n/a)" },
        resolvePackage = { if (it in resolved) "pkg.$it" else null }
    )

    @Test
    fun `only the shortcuts the device answers are offered`() {
        val options = listOf(ShortcutAction.AllApps, ShortcutAction.systemIntent("Calendar"), ShortcutAction.systemIntent("Camera"))

        val list = defaultShortcutCandidates(options, ShortcutAction.AllApps, environment(setOf("Calendar")))

        assertEquals(listOf(ShortcutAction.AllApps, ShortcutAction.systemIntent("Calendar")), list.map { it.action })
        assertTrue(list.all { it.isAvailable })
    }

    @Test
    fun `a saved shortcut nobody answers stays in the list, marked unavailable`() {
        val camera = ShortcutAction.systemIntent("Camera")

        val list = defaultShortcutCandidates(listOf(ShortcutAction.AllApps, camera), camera, environment(emptySet()))

        val saved = list.single { it.action == camera }
        assertFalse(saved.isAvailable)
        assertEquals("Camera (n/a)", saved.label)
    }
}
