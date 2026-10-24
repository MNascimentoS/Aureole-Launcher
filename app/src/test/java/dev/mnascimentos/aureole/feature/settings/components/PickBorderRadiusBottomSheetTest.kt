package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PickBorderRadiusBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering PickBorderRadiusBottomSheet, should display header and options`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickBorderRadiusBottomSheet(
                    selectedRadiusDp = 16,
                    onRadiusSelected = {},
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Change Border").assertIsDisplayed()
        composeTestRule.onNodeWithText("None (0 dp)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Standard (16 dp)").assertIsDisplayed()
    }

    @Test
    fun `when option is selected, should invoke onRadiusSelected with correct radius`() {
        var selectedRadius = -1

        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickBorderRadiusBottomSheet(
                    selectedRadiusDp = 16,
                    onRadiusSelected = { selectedRadius = it },
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Compact (8 dp)").performClick()
        assertEquals(8, selectedRadius)

        composeTestRule.onNodeWithText("Large (24 dp)").performClick()
        assertEquals(24, selectedRadius)
    }
}
