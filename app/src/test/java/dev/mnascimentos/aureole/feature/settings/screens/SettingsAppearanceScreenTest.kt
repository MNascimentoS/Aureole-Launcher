package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsAppearanceScreen, should display menu items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAppearanceScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Appearance").assertExists()
        composeTestRule.onNodeWithText("Set Background").assertExists()
        composeTestRule.onNodeWithText("Glass effect").assertExists()
        composeTestRule.onNodeWithText("Change font").assertExists()
        composeTestRule.onNodeWithText("Color palette").assertExists()
        composeTestRule.onNodeWithText("Custom colors").assertExists()
        composeTestRule.onNodeWithText("Change Border").assertExists()
        composeTestRule.onNodeWithText("Themed icons").assertExists()
    }

    @Test
    fun `when custom wallpaper is set, should display scale type row and cycle scale selection`() {
        var selectedScale = ""
        val state = SettingsUiState(isCustomWallpaperSet = true, wallpaperScaleType = "Crop")
        val actions = SettingsScreenActions(onSelectWallpaperScaleType = { selectedScale = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAppearanceScreen(
                    uiState = state,
                    actions = actions,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Image scale").assertExists()
        composeTestRule.onNodeWithText("Crop").assertExists()
        composeTestRule.onNodeWithText("Image scale").performScrollTo().performClick()
        assertEquals("Fit", selectedScale)
    }

    @Test
    fun `when clicking Set Background, should open set background bottom sheet`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAppearanceScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Set Background").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Solid color").assertExists()
    }

    @Test
    fun `when clicking Custom colors, should trigger open color picker action`() {
        var openColorPickerCalled = false
        val actions = SettingsScreenActions(onOpenColorPickerDialog = { openColorPickerCalled = true })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAppearanceScreen(
                    uiState = SettingsUiState(),
                    actions = actions,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Custom colors").performScrollTo().performClick()
        assertTrue(openColorPickerCalled)
    }

    @Test
    fun `when toggling Themed icons, should call onToggleThemedAppIcons action`() {
        var themedIconsToggled = false
        val actions = SettingsScreenActions(onToggleThemedAppIcons = { themedIconsToggled = true })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAppearanceScreen(
                    uiState = SettingsUiState(),
                    actions = actions,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Themed icons").performScrollTo().performClick()
        assertTrue(themedIconsToggled)
    }
}
