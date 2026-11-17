package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
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
    fun `closing after tapping a palette saves it once and closes`() {
        renderPalette()

        composeTestRule.onNodeWithText(allPalettes[1].name).performClick()
        composeTestRule.closeOverlayWithBack()

        assertEquals(listOf(allPalettes[1].name), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `closing without a choice only dismisses the palette sheet`() {
        renderPalette()

        composeTestRule.closeOverlayWithBack()

        assertEquals(emptyList<String>(), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `closing without a choice only dismisses the font sheet`() {
        renderFont()

        composeTestRule.closeOverlayWithBack()

        assertEquals(emptyList<String>(), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `tapping a font does not call the callback until the sheet closes`() {
        renderFont()

        composeTestRule.onNodeWithText("Roboto").performClick()
        assertEquals(emptyList<String>(), applied)

        composeTestRule.closeOverlayWithBack()

        assertEquals(listOf("Roboto"), applied)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `tapping back to the font already applied closes without saving`() {
        renderFont()

        composeTestRule.onNodeWithText("Roboto").performClick()
        composeTestRule.onNodeWithText("Istok Web").performClick()
        composeTestRule.closeOverlayWithBack()

        assertEquals(emptyList<String>(), applied)
        assertEquals(1, dismissCount)
    }
}
