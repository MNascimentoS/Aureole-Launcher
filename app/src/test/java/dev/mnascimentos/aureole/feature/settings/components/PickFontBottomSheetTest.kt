package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PickFontBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering PickFontBottomSheet, should display title and font list`() {
        val firstFont = AvailableFonts.keys.first()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickFontBottomSheet(
                    selectedFontName = firstFont,
                    onFontSelected = {},
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Pick Font").assertIsDisplayed()
        composeTestRule.onNodeWithText(firstFont).assertIsDisplayed()
    }

    @Test
    fun `when font item is clicked, should invoke onFontSelected`() {
        var selectedFont: String? = null
        val targetFont = AvailableFonts.keys.elementAtOrElse(1) { "Istok Web" }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickFontBottomSheet(
                    selectedFontName = "Istok Web",
                    onFontSelected = { selectedFont = it },
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText(targetFont).performClick()
        assertEquals(targetFont, selectedFont)
    }
}
