package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.components.SettingsChildSheet
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

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

        composeTestRule.onNodeWithText("Change border").assertExists()
        composeTestRule.onNodeWithText("16 dp").assertExists()
        composeTestRule.onNodeWithText("Change border").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when cardsGlass sheet is open, should apply every tap on the spot`() {
        val applied = mutableListOf<CardsGlassDraft>()
        val actions = SettingsScreenActions(onApplyCardsGlass = { applied += it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsChildSheet(
                    kind = SettingsChildKind.CARDS_GLASS,
                    uiState = SettingsUiState(cornerRadiusDp = 16, isHazeEnabled = true),
                    actions = actions,
                    onClose = {}
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
    fun `when closing the cardsGlass sheet, should close and hand back the opening values`() {
        var closedWith: CardsGlassDraft? = null
        var dismissCalled = false
        val opening = SettingsUiState(cornerRadiusDp = 16, isHazeEnabled = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsChildSheet(
                    kind = SettingsChildKind.CARDS_GLASS,
                    uiState = opening,
                    actions = SettingsScreenActions(onCardsGlassClosed = { closedWith = it }),
                    onClose = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("8").performScrollTo().performClick()
        composeTestRule.closeOverlayWithBack()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(opening.toCardsGlassDraft(), closedWith)
        assertTrue(dismissCalled)
    }

    @Test
    fun `the cardsGlass sheet has no Apply or Cancel button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsChildSheet(
                    kind = SettingsChildKind.CARDS_GLASS,
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onClose = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Apply").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancel").assertDoesNotExist()
    }
}
