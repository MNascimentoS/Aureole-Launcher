package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PickThemeSheetsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val applied = mutableListOf<String>()
    private var dismissCount = 0

    private fun renderPalette() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickPaletteBottomSheet(
                    selectedPaletteName = "Frostbite",
                    onPaletteSelected = { palette: ThemePalette -> applied += palette.name },
                    onDismissRequest = { dismissCount++ }
                )
            }
        }
    }

    private fun renderFont() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickFontBottomSheet(
                    selectedFontName = "Istok Web",
                    onFontSelected = { applied += it },
                    onDismissRequest = { dismissCount++ }
                )
            }
        }
    }

    @Test
    fun `tapping a palette does not call the callback`() {
        renderPalette()

        composeTestRule.onNodeWithText(allPalettes[1].name).performClick()

        assertEquals(emptyList<String>(), applied)
        assertEquals(0, dismissCount)
    }

    @Test
    fun `Apply calls the callback once with the tapped palette and closes`() {
        renderPalette()

        composeTestRule.onNodeWithText(allPalettes[1].name).performClick()
        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf(allPalettes[1].name), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `Cancel after tapping a palette only dismisses`() {
        renderPalette()

        composeTestRule.onNodeWithText(allPalettes[1].name).performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitForIdle()

        assertEquals(emptyList<String>(), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `Apply without a choice applies the palette already shown as selected`() {
        renderPalette()

        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, applied.size)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `Apply without a choice applies the font already shown as selected`() {
        renderFont()

        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, applied.size)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `tapping a font does not call the callback until Apply`() {
        renderFont()

        composeTestRule.onNodeWithText("Roboto").performClick()
        assertEquals(emptyList<String>(), applied)

        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf("Roboto"), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `Cancel after tapping a font only dismisses`() {
        renderFont()

        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitForIdle()

        assertEquals(emptyList<String>(), applied)
        assertEquals(1, dismissCount)
    }
}
