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

@RunWith(RobolectricTestRunner::class)
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
    fun `when a palette is tapped, should apply it right away`() {
        val palette = allPalettes.first().name
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText(palette).performClick()

        assertEquals(listOf("palette=$palette"), calls)
    }

    @Test
    fun `when a font is tapped and looks good confirmed, should apply and confirm`() {
        val font = AvailableFonts.keys.first()
        render(AppearancePicker.FONT)

        composeTestRule.onNodeWithText(font).performClick()
        composeTestRule.onNodeWithText("Looks good").performClick()

        assertEquals(listOf("font=$font", "confirm"), calls)
    }

    @Test
    fun `should invite the user to try the options`() {
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText("Tap to see your screen change right away.").assertExists()
    }

    @Test
    fun `when no picker is active, should show nothing`() {
        render(picker = null)

        composeTestRule.onNodeWithText("Looks good").assertDoesNotExist()
    }
}
