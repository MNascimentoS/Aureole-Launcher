package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingPresetScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val selected = mutableListOf<LayoutPreset>()
    private val applied = mutableListOf<LayoutPreset>()
    private var closes = 0

    private fun render(
        preset: LayoutPreset = LayoutPreset.MINIMAL,
        plans: List<LayoutPresetPlan> = emptyList(),
        isLeftHanded: Boolean = false
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingPresetScreen(
                    onboarding = OnboardingUiState(
                        isChoosingPreset = true,
                        selectedPreset = preset,
                        presetPlans = plans
                    ),
                    actions = OnboardingActions(
                        onSelectPreset = { selected += it },
                        onApplyPreset = { applied += it },
                        onClosePresets = { closes++ }
                    ),
                    isLeftHanded = isLeftHanded
                )
            }
        }
    }

    @Test
    fun `when shown, should list the three presets`() {
        render()

        composeTestRule.onNodeWithText("Pick a starting point").assertExists()
        composeTestRule.onNodeWithText("Minimal").assertExists()
        composeTestRule.onNodeWithText("Productive").assertExists()
        composeTestRule.onNodeWithText("Widgets").assertExists()
    }

    @Test
    fun `when a card is tapped, should select that preset`() {
        render()

        composeTestRule.onNodeWithText("Productive").performScrollTo().performClick()

        assertEquals(listOf(LayoutPreset.PRODUCTIVE), selected)
    }

    @Test
    fun `when use is tapped, should apply the selected preset`() {
        render(preset = LayoutPreset.WIDGETS, isLeftHanded = true)

        composeTestRule.onNodeWithText("Use Widgets").performClick()

        assertEquals(listOf(LayoutPreset.WIDGETS), applied)
    }

    @Test
    fun `when back is tapped, should close the presets`() {
        render(plans = listOf(LayoutPresetPlan(LayoutPreset.MINIMAL, favorites = listOf("a", "b"))))

        composeTestRule.onNodeWithContentDescription("Back", substring = true, ignoreCase = true).performClick()

        assertEquals(1, closes)
    }
}
