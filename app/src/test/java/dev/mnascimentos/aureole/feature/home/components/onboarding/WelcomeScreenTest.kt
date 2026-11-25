package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WelcomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var starts = 0
    private var skips = 0
    private var presets = 0

    private fun render(canUsePreset: Boolean = false) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                WelcomeScreen(
                    onStart = { starts++ },
                    onSkipIntro = { skips++ },
                    onUsePreset = if (canUsePreset) ({ presets++ }) else null
                )
            }
        }
    }

    @Test
    fun `when the home is empty, should offer a ready-made layout`() {
        render(canUsePreset = true)

        composeTestRule.onNodeWithText("Use a ready-made layout").performClick()

        assertEquals(1, presets)
        assertEquals(0, starts)
    }

    @Test
    fun `when the home already has items, should not offer a ready-made layout`() {
        render(canUsePreset = false)

        composeTestRule.onNodeWithText("Use a ready-made layout").assertDoesNotExist()
    }

    @Test
    fun `when start is tapped, should play the exit and then start`() {
        render()

        composeTestRule.onNodeWithText("Start from scratch").performClick()
        composeTestRule.mainClock.advanceTimeBy(EXIT_WAIT_MS)
        composeTestRule.waitForIdle()

        assertEquals(1, starts)
        assertEquals(0, skips)
    }

    @Test
    fun `when skip is tapped, should skip the intro`() {
        render()

        composeTestRule.onNodeWithText("Skip intro").performClick()

        assertEquals(1, skips)
        assertEquals(0, starts)
    }

    @Test
    fun `when shown, should present the title and subtitle`() {
        render()

        composeTestRule.onNodeWithText("Welcome to Aureole").assertExists()
        composeTestRule.onNodeWithText("A light, beautiful home screen", substring = true).assertExists()
    }

    private companion object {
        const val EXIT_WAIT_MS = 2_000L
    }
}
