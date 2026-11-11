package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class OnboardingAppearancePickerComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val calls = mutableListOf<String>()
    private val actions = OnboardingActions(
        onSelectPalette = { calls += "palette=$it" },
        onSelectFont = { calls += "font=$it" },
        onConfirmAppearancePicker = { calls += "confirm" }
    )

    private fun render(picker: AppearancePicker?) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingAppearancePickerComponent(
                    picker = picker,
                    selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
                    actions = actions
                )
            }
        }
    }

    @Test
    fun `when a palette is tapped, should only preview it until Apply`() {
        val palette = allPalettes.first().name
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText(palette).performClick()
        assertEquals(emptyList<String>(), calls)

        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf("palette=$palette", "confirm"), calls)
    }

    @Test
    fun `when a font is tapped and Apply is pressed, should apply it and confirm`() {
        val font = AvailableFonts.keys.first()
        render(AppearancePicker.FONT)

        composeTestRule.onNodeWithText(font).performClick()
        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf("font=$font", "confirm"), calls)
    }

    @Test
    fun `should invite the user to try the options`() {
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText("Tap a palette or font to try it. Nothing changes until you tap Apply.").assertExists()
    }

    @Test
    fun `when no picker is active, should show nothing`() {
        render(picker = null)

        composeTestRule.onNodeWithText("Got it").assertDoesNotExist()
    }
}
