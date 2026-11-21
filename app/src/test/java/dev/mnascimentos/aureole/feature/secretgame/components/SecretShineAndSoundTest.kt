package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SecretShineAndSoundTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when the shine is at each stage, should render the thank you text`() {
        var shine by mutableStateOf(SecretShineState())
        rule.setContent { AureoleLauncherTheme { SecretThankYou(shine = shine) } }

        listOf(SecretShineState(sweep = 0.5f), SecretShineState(star = 0.3f), SecretShineState(0.5f, 0.7f))
            .forEach {
                shine = it
                rule.waitForIdle()
                rule.onRoot().captureToImage()
            }

        rule.onNodeWithText("Thank you!").assertExists()
    }

    @Test
    fun `when the shine is enabled, should run to the end without failing`() {
        var shine: SecretShineState? = null
        rule.setContent {
            AureoleLauncherTheme {
                val state = rememberSecretShine(enabled = true)
                shine = state
                SecretThankYou(shine = state)
            }
        }

        rule.mainClock.advanceTimeBy(SHINE_TOTAL_MS)

        assertFalse(shine!!.isCancelled)
    }

    @Test
    fun `when the shine is cancelled, should hide it`() {
        var shine: SecretShineState? = null
        rule.setContent {
            AureoleLauncherTheme {
                val state = rememberSecretShine(enabled = true)
                shine = state
                SecretThankYou(shine = state)
            }
        }

        shine!!.cancel()
        rule.waitForIdle()

        assertTrue(shine!!.isCancelled)
        assertEquals(0f, shine!!.sweep, 0f)
    }

    @Test
    fun `when pressing the sound button, should ask to toggle`() {
        var toggles = 0
        rule.setContent {
            AureoleLauncherTheme { SecretSoundButton(isOn = true, onToggle = { toggles++ }) }
        }

        rule.onNodeWithContentDescription("Sound on").performClick()

        assertEquals(1, toggles)
    }

    @Test
    fun `when sound is off, should show the off label`() {
        rule.setContent {
            AureoleLauncherTheme { SecretSoundButton(isOn = false, onToggle = {}) }
        }

        rule.onNodeWithContentDescription("Sound off").assertExists()
    }

    private companion object {
        const val SHINE_TOTAL_MS = 4_000L
    }
}
