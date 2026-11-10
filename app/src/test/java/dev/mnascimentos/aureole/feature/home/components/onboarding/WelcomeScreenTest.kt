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

    private fun render() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                WelcomeScreen(onStart = { starts++ }, onSkipIntro = { skips++ })
            }
        }
    }

    @Test
    fun `when start is tapped, should play the exit and then start`() {
        render()

        composeTestRule.onNodeWithText("Let's begin").performClick()
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
