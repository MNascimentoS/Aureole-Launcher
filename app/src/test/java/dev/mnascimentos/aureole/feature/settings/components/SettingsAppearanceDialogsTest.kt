package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceDialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SetBackgroundBottomSheet, should handle solid color and custom image clicks`() {
        var solidColorClicked = false
        var customImageClicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SetBackgroundBottomSheet(
                    onSolidColorClick = { solidColorClicked = true },
                    onCustomImageClick = { customImageClicked = true },
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Set background").assertIsDisplayed()
        composeTestRule.onNodeWithText("Solid color").performClick()
        assertTrue(solidColorClicked)

        composeTestRule.onNodeWithText("Custom image").performClick()
        assertTrue(customImageClicked)
    }

    @Test
    fun `when rendering PickPaletteBottomSheet, should display title and allow selecting palette`() {
        var selectedPalette: ThemePalette? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickPaletteBottomSheet(
                    selectedPaletteName = "Frostbite",
                    onPaletteSelected = { selectedPalette = it },
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Pick a palette").assertIsDisplayed()
        composeTestRule.onNodeWithText("Frostbite").performClick()
        assertNotNull(selectedPalette)
    }

    @Test
    fun `when rendering BlurBottomSheet, should display blur options and handle selection`() {
        var selectedOpacity = -1f
        var selectedHazeState = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                BlurBottomSheet(
                    currentOpacity = 0.5f,
                    isHazeEnabled = true,
                    onOptionSelected = { opacity, haze ->
                        selectedOpacity = opacity
                        selectedHazeState = haze
                    },
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Glass & Blur").assertIsDisplayed()
        composeTestRule.onNodeWithText("None").performClick()
        assertEquals(0.0f, selectedOpacity)
        assertFalse(selectedHazeState)

        composeTestRule.onNodeWithText("Max").performClick()
        assertEquals(0.9f, selectedOpacity)
        assertTrue(selectedHazeState)
    }
}
