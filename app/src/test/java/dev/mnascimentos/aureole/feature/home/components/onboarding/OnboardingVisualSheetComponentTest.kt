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
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.VisualChoice
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class OnboardingVisualSheetComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val closes = mutableListOf<VisualChoice>()
    private val actions = OnboardingActions(onCloseVisual = { closes += it })

    private fun render(picker: AppearancePicker?) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingVisualSheetComponent(
                    picker = picker,
                    selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
                    actions = actions,
                    nowMillis = 0L
                )
            }
        }
    }

    @Test
    fun `when a palette is tapped, should only preview it until the sheet closes`() {
        val palette = allPalettes[1].name
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText(palette).performClick()
        assertEquals(emptyList<VisualChoice>(), closes)

        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        // Saved by its base name, as the palette picker does, even when the light variant was shown.
        assertEquals(listOf(VisualChoice(paletteName = palette)), closes)
    }

    @Test
    fun `when the Font tab is chosen and a font tapped, should save it on close`() {
        val font = AvailableFonts.keys.elementAt(1)
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText("Font").performClick()
        composeTestRule.onNodeWithText(font).performClick()
        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        assertEquals(listOf(VisualChoice(fontName = font)), closes)
    }

    @Test
    fun `when the sheet closes without a tap, should still close the step once`() {
        render(AppearancePicker.PALETTE)

        composeTestRule.onNodeWithText("Look").assertExists()
        composeTestRule.closeOverlayWithBack()
        composeTestRule.waitForIdle()

        assertEquals(listOf(VisualChoice()), closes)
    }

    @Test
    fun `when no sheet is open, should show nothing`() {
        render(picker = null)

        composeTestRule.onNodeWithText("Look").assertDoesNotExist()
    }
}
