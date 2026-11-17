package dev.mnascimentos.aureole.feature.settings.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.ENTRY_TAP_GUARD_MS
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
class BehaviorSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun render(actions: SettingsScreenActions = SettingsScreenActions()) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    BehaviorSections(uiState = SettingsUiState(), actions = actions)
                }
            }
        }
    }

    @Test
    fun `does not list Check for updates, which moved to Advanced`() {
        render()
        composeTestRule.onNodeWithText("Check for updates").assertDoesNotExist()
    }

    @Test
    fun `lists the two groups with their rows`() {
        render()
        listOf(
            "GESTURES AND APP LIST",
            "Home opens all apps",
            "All apps on left side",
            "Disable alphabet scrubber",
            "HOME SCREEN",
            "Show widget indicator",
            "Header offset"
        ).forEach { composeTestRule.onNodeWithText(it).assertExists() }
        val gestures = composeTestRule.onNodeWithText("GESTURES AND APP LIST").getUnclippedBoundsInRoot().top
        val home = composeTestRule.onNodeWithText("HOME SCREEN").getUnclippedBoundsInRoot().top
        assertTrue(gestures < home)
    }

    @Test
    fun `toggles fire their actions after the entry guard`() {
        var home = false
        var left = false
        var dots = false
        var scrubber = false
        render(
            SettingsScreenActions(
                onToggleHomeButtonOpensAllApps = { home = true },
                onToggleLeftHandedMode = { left = true },
                onToggleShowWidgetDots = { dots = true },
                onToggleDisableAlphabetScrubber = { scrubber = true }
            )
        )
        composeTestRule.onNodeWithText("Disable alphabet scrubber").performScrollTo().performClick()
        assertTrue(!scrubber)

        ShadowSystemClock.advanceBy(Duration.ofMillis(ENTRY_TAP_GUARD_MS))
        composeTestRule.onNodeWithText("Home opens all apps").performScrollTo().performClick()
        composeTestRule.onNodeWithText("All apps on left side").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Show widget indicator").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Disable alphabet scrubber").performScrollTo().performClick()
        assertTrue(home && left && dots && scrubber)
    }
}
