package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class QuickButtonLaunchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val application: Application = RuntimeEnvironment.getApplication()
    private var settingsOpened = false
    private var clickAction by mutableStateOf(ShortcutAction.None)

    private fun showButton() {
        val actions = createDefaultHomeScreenActions().copy(onSettingsClick = { settingsOpened = true })
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = LauncherItemState(
                        id = "qb",
                        quickButtonModel = QuickButtonModel(
                            id = "qb",
                            shortcuts = QuickButtonShortcuts(click = clickAction)
                        )
                    ),
                    actions = actions
                )
            }
        }
    }

    /** Taps the button with [action] as its click shortcut and returns the activity it started, if any. */
    private fun tapWith(action: ShortcutAction): Intent? {
        clickAction = action
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().performClick()
        composeTestRule.waitForIdle()
        return shadowOf(application).nextStartedActivity
    }

    @Test
    fun `when a system shortcut is tapped, should open the matching system screen in a new task`() {
        showButton()

        val browser = tapWith(ShortcutAction.systemIntent("Browser"))
        assertEquals(Intent.ACTION_VIEW, browser?.action)
        assertEquals("http://www.google.com", browser?.dataString)
        assertTrue((browser?.flags ?: 0) and Intent.FLAG_ACTIVITY_NEW_TASK != 0)

        assertEquals(
            MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA,
            tapWith(ShortcutAction.systemIntent("Camera"))?.action
        )
        assertEquals(Intent.ACTION_DIAL, tapWith(ShortcutAction.systemIntent("Dialer"))?.action)
        assertEquals(Settings.ACTION_SETTINGS, tapWith(ShortcutAction.systemIntent("Anything else"))?.action)
    }

    @Test
    fun `when the assistant shortcut is tapped, should start the voice command`() {
        showButton()

        assertEquals(Intent.ACTION_VOICE_COMMAND, tapWith(ShortcutAction.Assistant)?.action)
    }

    @Test
    fun `when an installed app shortcut is tapped, should launch that app`() {
        val component = ComponentName("com.example.mail", "com.example.mail.Main")
        shadowOf(application.packageManager).apply {
            addActivityIfNotPresent(component)
            addIntentFilterForActivity(
                component,
                IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
            )
        }
        showButton()

        val started = tapWith(ShortcutAction.openApp("com.example.mail"))

        assertEquals("com.example.mail", started?.component?.packageName ?: started?.`package`)
    }

    @Test
    fun `when an app shortcut has no target or the app is missing, should not start anything`() {
        showButton()

        assertNull(tapWith(ShortcutAction.openApp("")))
        assertNull(tapWith(ShortcutAction.openApp("com.not.installed")))
    }

    @Test
    fun `when the settings shortcut is tapped, should open the launcher settings`() {
        showButton()

        tapWith(ShortcutAction.Settings)

        assertTrue(settingsOpened)
    }
}
