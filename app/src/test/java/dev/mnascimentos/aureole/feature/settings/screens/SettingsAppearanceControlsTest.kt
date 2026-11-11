package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L
private const val DEFAULT_SEED = 0xFF112233.toInt()

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceControlsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when GlassAndBlurRow has haze disabled, should display None`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = false),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("None").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has low level, should display Light`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeBlurLevel = GlassLevel.LOW),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Light").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has medium level, should display Medium`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeBlurLevel = GlassLevel.MEDIUM),
                    onClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Medium").assertExists()
    }

    @Test
    fun `when GlassAndBlurRow has max level, should display Max`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GlassAndBlurRow(
                    uiState = SettingsUiState(isHazeEnabled = true, hazeBlurLevel = GlassLevel.MAX),
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
        composeTestRule.onNodeWithText("Apply").performClick()
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
        composeTestRule.onNodeWithText("Apply").performClick()
        assertEquals("Istok Web", selectedFont)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when setBg flag is true, applying the solid color should save the seed color`() {
        var savedColor: Int? = null
        var dismissCalled = false
        val flags = AppearanceDialogFlags(showSetBg = true)
        val actions = SettingsScreenActions(
            onSelectManualSeedColor = { savedColor = it }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = flags,
                    uiState = SettingsUiState(manualSeedColor = DEFAULT_SEED),
                    actions = actions,
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertEquals(DEFAULT_SEED, savedColor)
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
        composeTestRule.onNodeWithText("Apply").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertTrue(wallpaperChangeClicked)
        assertTrue(dismissCalled)
    }

    @Test
    fun `when cardsGlass flag is true, should apply every tap on the spot`() {
        val applied = mutableListOf<CardsGlassDraft>()
        val actions = SettingsScreenActions(onApplyCardsGlass = { applied += it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = AppearanceDialogFlags(showCardsGlass = true),
                    uiState = SettingsUiState(cornerRadiusDp = 16, isHazeEnabled = true),
                    actions = actions,
                    onDismissDialog = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Cards and glass").assertExists()
        composeTestRule.onNodeWithText("8").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Max").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Solid").performScrollTo().performClick()

        assertEquals(CardsGlassDraft(radiusDp = 8, glass = GlassLevel.MAX, opacity = 0.9f), applied.last())
        assertEquals(3, applied.size)
    }

    @Test
    fun `when tapping Done on the cardsGlass sheet, should close and hand back the opening values`() {
        var closedWith: CardsGlassDraft? = null
        var dismissCalled = false
        val opening = SettingsUiState(cornerRadiusDp = 16, isHazeEnabled = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = AppearanceDialogFlags(showCardsGlass = true),
                    uiState = opening,
                    actions = SettingsScreenActions(onCardsGlassClosed = { closedWith = it }),
                    onDismissDialog = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("8").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(opening.toCardsGlassDraft(), closedWith)
        assertTrue(dismissCalled)
    }

    @Test
    fun `the cardsGlass sheet has no Apply or Cancel button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceDialogs(
                    flags = AppearanceDialogFlags(showCardsGlass = true),
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onDismissDialog = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Apply").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancel").assertDoesNotExist()
    }
}
