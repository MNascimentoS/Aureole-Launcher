package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceControlsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when GlassAndBlurRow has haze disabled, should display None`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = false, hazeOpacity = 0.0f),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("None").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has low opacity, should display Low`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeOpacity = 0.20f),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Low").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has medium opacity, should display Medium`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeOpacity = 0.50f),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Medium").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has max opacity, should display Max`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeOpacity = 0.90f),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Max").assertExists()
    }

    @Test
    fun `when rendering ChangeBorderRow, should display radius text and trigger click`() {
        var clicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ChangeBorderRow(
                    cornerRadiusDp = 16,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Change Border").assertExists()
        composeTestRule.onNodeWithText("16 dp").assertExists()
        composeTestRule.onNodeWithText("Change Border").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when palette flag is true, should render PickPaletteBottomSheet and handle selection`() {
        var selectedTheme = ""
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showPalette = true)
        val actions = SettingsScreenActions(onSelectTheme = { selectedTheme = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Pick a palette").assertExists()
        composeTestRule.onNodeWithText("Frostbite").performClick()
        assertEquals("Frostbite", selectedTheme)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when font flag is true, should render PickFontBottomSheet and handle selection`() {
        var selectedFont = ""
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showFont = true)
        val actions = SettingsScreenActions(onSelectFont = { selectedFont = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Pick Font").assertExists()
        composeTestRule.onNodeWithText("Istok Web").performClick()
        assertEquals("Istok Web", selectedFont)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when setBg flag is true, clicking solid color should trigger color picker`() {
        var colorPickerOpened = false
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showSetBg = true)
        val actions = SettingsScreenActions(
            onOpenColorPickerDialog = { colorPickerOpened = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Solid color").performClick()
        assertTrue(colorPickerOpened)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when setBg flag is true, clicking custom image should trigger wallpaper change`() {
        var wallpaperChangeClicked = false
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showSetBg = true)
        val actions = SettingsScreenActions(
            onChangeWallpaperClick = { wallpaperChangeClicked = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Custom image").performClick()
        assertTrue(wallpaperChangeClicked)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when blur flag is true, should handle blur selection and toggle haze state`() {
        var toggleHazeCalled = false
        var hazeOpacity = -1f
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showBlur = true)
        val state = SettingsUiState(isHazeEnabled = false, hazeOpacity = 0.0f)
        val actions = SettingsScreenActions(
            onToggleHaze = { toggleHazeCalled = true },
            onHazeOpacitySelected = { hazeOpacity = it }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = state,
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Medium").performClick()
        assertTrue(toggleHazeCalled)
        assertEquals(0.50f, hazeOpacity)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when blur flag is true and haze is enabled, disabling blur should toggle haze and set opacity to 0`() {
        var toggleHazeCalled = false
        var hazeOpacity = -1f
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showBlur = true)
        val state = SettingsUiState(isHazeEnabled = true, hazeOpacity = 0.5f)
        val actions = SettingsScreenActions(
            onToggleHaze = { toggleHazeCalled = true },
            onHazeOpacitySelected = { hazeOpacity = it }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = state,
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("None").performClick()
        assertTrue(toggleHazeCalled)
        assertEquals(0.0f, hazeOpacity)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when borderRadius flag is true, should render PickBorderRadiusBottomSheet and handle selection`() {
        var selectedRadius = -1
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showBorderRadius = true)
        val actions = SettingsScreenActions(onSelectCornerRadius = { selectedRadius = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Change Border").assertExists()
        composeTestRule.onNodeWithText("Standard (16 dp)").performClick()
        assertEquals(16, selectedRadius)
        assertTrue(dismissCalled)
    }
}
