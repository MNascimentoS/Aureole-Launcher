package dev.mnascimentos.aureole.core.designsystem.palette

import android.app.ActivityManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.utils.HazeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class PaletteSelectionTest {

    @Test
    fun `when a known palette is requested, should return it in dark or light`() {
        val dark = getPaletteByName(frostbitePalette.name)
        val light = getPaletteByName("${frostbitePalette.name} Light", isDarkTheme = false)

        assertEquals(frostbitePalette, dark)
        assertEquals(frostbitePalette.toLight(), light)
        assertEquals(light, getLightPaletteByName(frostbitePalette.name))
    }

    @Test
    fun `when an unknown palette is requested, should fall back to Frostbite`() {
        assertEquals(frostbitePalette, getPaletteByName("Does not exist"))
    }

    @Test
    fun `when the custom palette is requested, should build it from the seed color`() {
        val seed = Color(0xFF1B3A5C).toArgb()

        val custom = getPaletteByName("custom", seedColor = seed)
        val customLight = getPaletteByName("Custom Light", isDarkTheme = false, seedColor = seed)

        assertEquals("Custom", custom.name)
        assertEquals("Custom Light", customLight.name)
        assertEquals(Color(seed), custom.colors.background)
        assertEquals("Custom", getPaletteByName("Custom").name)
    }

    @Test
    fun `when the seed is dark or light, should pick readable text colors`() {
        val onDark = createPaletteFromSeedColor(Color(0xFF101418).toArgb()).colors
        val onLight = createPaletteFromSeedColor(Color(0xFFF2F2F2).toArgb(), isDarkTheme = false).colors

        assertEquals(Color.White, onDark.onSurfaceHigh)
        assertNotEquals(Color.White, onLight.onSurfaceHigh)
    }

    @Test
    fun `when shortcuts are shown, should use a readable name for each type`() {
        assertEquals("None", ShortcutAction.None.displayName)
        assertEquals("All apps", ShortcutAction.AllApps.displayName)
        assertEquals("Assistant", ShortcutAction.Assistant.displayName)
        assertEquals("Aureole Settings", ShortcutAction.Settings.displayName)
        assertEquals("Notifications", ShortcutAction.Notifications.displayName)
        assertEquals("Open App", ShortcutAction.openApp("com.mail").displayName)
        assertEquals("Camera", ShortcutAction.systemIntent("Camera").displayName)
        assertEquals("System Action", ShortcutAction(ShortcutType.SYSTEM_INTENT).displayName)
    }

    @Test
    fun `when the device has enough memory on Android 12+, should support haze`() {
        assertTrue(HazeUtils.isDeviceHazeSupported(RuntimeEnvironment.getApplication()))
    }

    @Test
    fun `when the device is low on RAM, should not support haze`() {
        val context = RuntimeEnvironment.getApplication()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        shadowOf(activityManager).setIsLowRamDevice(true)

        assertFalse(HazeUtils.isDeviceHazeSupported(context))
    }

    @Test
    @Config(sdk = [30])
    fun `when the device runs Android 11, should not support haze`() {
        assertFalse(HazeUtils.isDeviceHazeSupported(RuntimeEnvironment.getApplication()))
    }
}
