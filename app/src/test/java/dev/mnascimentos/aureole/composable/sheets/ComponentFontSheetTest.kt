package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ComponentFontSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val selected = mutableListOf<String>()
    private val boldChanges = mutableListOf<Boolean>()
    private var dismissCount = 0

    private fun renderComponent(selectedFontName: String = FONT_KEY_LAUNCHER) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickFontBottomSheet(
                    selectedFontName = selectedFontName,
                    onFontSelected = { selected += it },
                    onDismissRequest = { dismissCount++ },
                    options = ThemePickerSheetOptions(
                        componentFont = ComponentFontOptions(
                            title = "Clock font",
                            subtitle = "Only for this clock",
                            sectionLabel = "Clock font",
                            sample = "16:16",
                            isBold = true,
                            onBoldChange = { boldChanges += it }
                        )
                    )
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `opened by a component, shows its header, the bold switch and the launcher font marked first`() {
        renderComponent()

        composeTestRule.onNodeWithText("Only for this clock").assertIsDisplayed()
        composeTestRule.onNodeWithText("CLOCK FONT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bold").assertIsDisplayed()
        composeTestRule.onNodeWithText("Launcher font").assertIsSelected()
    }

    @Test
    fun `flipping the bold switch applies at once and keeps the sheet open`() {
        renderComponent()

        composeTestRule.onNodeWithText("Bold").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf(false), boldChanges)
        assertEquals(0, dismissCount)
    }

    @Test
    fun `tapping a font applies it at once and closes the sheet`() {
        renderComponent()

        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.mainClock.advanceTimeBy(CLOSE_MS)
        composeTestRule.waitForIdle()

        assertEquals(listOf("Roboto"), selected)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `opened by Customize, has neither the launcher font nor the bold switch`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickFontBottomSheet(selectedFontName = "Istok Web", onFontSelected = {}, onDismissRequest = {})
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Launcher font").assertDoesNotExist()
        composeTestRule.onNodeWithText("Bold").assertDoesNotExist()
        composeTestRule.onNodeWithText("Istok Web").assertIsSelected()
    }

    private companion object {
        const val CLOSE_MS = 1_000L
    }
}
