package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoritesListRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering FavoritesEmptyHint, should display hint and respond to click`() {
        var clicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoritesEmptyHint(onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Tap to select Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to select Favorites").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering AllAppsDivider, should display divider text`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AllAppsDivider()
            }
        }

        composeTestRule.onNodeWithText("ALL APPS", substring = true).assertIsDisplayed()
    }
}
