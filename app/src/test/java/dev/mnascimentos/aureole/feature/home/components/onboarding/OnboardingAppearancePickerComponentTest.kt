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
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
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
        onConfirmAppearancePicker = { calls += "confirm" },
        onDismissAppearancePicker = { calls += "dismiss" }
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
    fun `when a palette is tapped, should only preview it until the sheet closes`() {
        val palette = allPalettes[1].name
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText(palette).performClick()
        assertEquals(emptyList<String>(), calls)

        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        assertEquals(listOf("palette=$palette", "confirm"), calls)
    }

    @Test
    fun `when a font is tapped and the sheet closes, should apply it and confirm`() {
        val font = AvailableFonts.keys.elementAt(1)
        render(AppearancePicker.FONT)

        composeTestRule.onNodeWithText(font).performClick()
        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        assertEquals(listOf("font=$font", "confirm"), calls)
    }

    @Test
    fun `when the sheet closes without a change, should give up without confirming`() {
        render(AppearancePicker.PALETTE)

        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        assertEquals(listOf("dismiss"), calls)
    }

    @Test
    fun `when no picker is active, should show nothing`() {
        render(picker = null)

        composeTestRule.onNodeWithText("Got it").assertDoesNotExist()
    }
}
