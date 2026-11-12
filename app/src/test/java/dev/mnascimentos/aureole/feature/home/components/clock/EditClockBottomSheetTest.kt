package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditClockBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private data class ClockActionsConfig(
        var onResetClockSettings: () -> Unit = {},
        var onUpdateClockStyle: (String) -> Unit = {},
        var onUpdateClockCustomGreeting: (String) -> Unit = {},
        var onUpdateClockAlignment: (String) -> Unit = {},
        var onUpdateClockFontFamily: (String) -> Unit = {},
        var onUpdateClockTimeFormat: (String) -> Unit = {},
        var onUpdateClockDateFormat: (String) -> Unit = {},
        var onUpdateClockTextColor: (Int) -> Unit = {},
        var onUpdateClockBackgroundColor: (Int) -> Unit = {},
        var onToggleClockBackground: () -> Unit = {}
    )

    private fun createDefaultActions(configure: ClockActionsConfig.() -> Unit = {}): HomeScreenActions {
        val config = ClockActionsConfig().apply(configure)
        return createDefaultHomeScreenActions().copy(
            onResetClockSettings = config.onResetClockSettings,
            onUpdateClockStyle = config.onUpdateClockStyle,
            onUpdateClockCustomGreeting = config.onUpdateClockCustomGreeting,
            onUpdateClockAlignment = config.onUpdateClockAlignment,
            onUpdateClockFontFamily = config.onUpdateClockFontFamily,
            onUpdateClockTimeFormat = config.onUpdateClockTimeFormat,
            onUpdateClockDateFormat = config.onUpdateClockDateFormat,
            onUpdateClockTextColor = config.onUpdateClockTextColor,
            onUpdateClockBackgroundColor = config.onUpdateClockBackgroundColor,
            onToggleClockBackground = config.onToggleClockBackground
        )
    }

    private fun advanceAnimation() {
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when edit clock bottom sheet is displayed, should render title and sections`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(
                    uiState = MainUiState(),
                    actions = createDefaultActions(),
                    onDismissRequest = {}
                )
            }
        }
        advanceAnimation()

        // Assert
        composeTestRule.onNodeWithText("Customize Clock").assertIsDisplayed()
        composeTestRule.onNodeWithText("STYLE").assertIsDisplayed()
        composeTestRule.onNodeWithText("ALIGNMENT").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText("Format").performClick()
        advanceAnimation()
        composeTestRule.onNodeWithText("TIME AND DATE FORMAT").assertIsDisplayed()
        composeTestRule.onNodeWithText("FONT STYLE").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText("Colors").performClick()
        advanceAnimation()
        composeTestRule.onNodeWithText("TEXT COLOR").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("CARD BACKGROUND").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when clicking reset defaults, should invoke onResetClockSettings`() {
        // Arrange
        var resetCalled = false
        val actions = createDefaultActions {
            onResetClockSettings = { resetCalled = true }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(
                    uiState = MainUiState(),
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }
        advanceAnimation()

        // Act
        composeTestRule.onNodeWithText("Reset").performClick()

        // Assert
        assertTrue(resetCalled)
    }

    @Test
    fun `when clicking save button, should invoke onDismissRequest after dismissal delay`() {
        // Arrange
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(
                    uiState = MainUiState(),
                    actions = createDefaultActions(),
                    onDismissRequest = { dismissed = true }
                )
            }
        }
        advanceAnimation()

        // Act
        composeTestRule.onNodeWithText("Save").performClick()
        advanceAnimation()

        // Assert
        assertTrue(dismissed)
    }

    @Test
    fun `when custom greeting style is selected, should show greeting text field`() {
        // Arrange
        var updatedGreeting = ""
        val actions = createDefaultActions {
            onUpdateClockCustomGreeting = { updatedGreeting = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(
                    uiState = MainUiState(
                        clockStyle = "CUSTOM_GREETING",
                        clockCustomGreeting = ""
                    ),
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }
        advanceAnimation()

        // Assert field is shown
        composeTestRule.onNodeWithText("Greeting Text").performScrollTo().assertIsDisplayed()

        // Act - type in text field specifically using hasSetTextAction
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Olá Amigo")

        // Assert callback
        assertEquals("Olá Amigo", updatedGreeting)
    }

    @Test
    fun `when selecting alignment options, should invoke onUpdateClockAlignment`() {
        // Arrange
        var selectedAlignment = ""
        val actions = createDefaultActions {
            onUpdateClockAlignment = { selectedAlignment = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockAlignmentSection(
                    uiState = MainUiState(clockAlignment = "START"),
                    actions = actions
                )
            }
        }

        // Act - click "Centro"
        composeTestRule.onNodeWithText("Center").performClick()

        // Assert
        assertEquals("CENTER", selectedAlignment)
    }

    @Test
    fun `when selecting font style chips, should invoke onUpdateClockFontFamily`() {
        // Arrange
        var selectedFont = ""
        val actions = createDefaultActions {
            onUpdateClockFontFamily = { selectedFont = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockFontSection(
                    uiState = MainUiState(clockFontFamily = "SANS_SERIF"),
                    actions = actions
                )
            }
        }

        // Act - click "Negrito"
        composeTestRule.onNodeWithText("Bold").performClick()

        // Assert
        assertEquals("BOLD", selectedFont)
    }

    @Test
    fun `when selecting time format choice buttons, should invoke onUpdateClockTimeFormat`() {
        // Arrange
        var selectedTimeFormat = ""
        val actions = createDefaultActions {
            onUpdateClockTimeFormat = { selectedTimeFormat = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockFormatSection(
                    uiState = MainUiState(clockTimeFormat = "SYSTEM"),
                    actions = actions
                )
            }
        }

        // Act - click "24 Horas"
        composeTestRule.onNodeWithText("24 Hours").performClick()

        // Assert
        assertEquals("24H", selectedTimeFormat)
    }

    @Test
    fun `when clicking text color swatch, should invoke onUpdateClockTextColor`() {
        // Arrange
        var selectedColor = -1
        val actions = createDefaultActions {
            onUpdateClockTextColor = { selectedColor = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockTextColorSection(
                    uiState = MainUiState(),
                    actions = actions
                )
            }
        }

        // Act - click "Azul Gelado" (the name comes from the string resource, so it follows the test locale)
        val iceBlue = ApplicationProvider.getApplicationContext<Context>().getString(R.string.home_clock_color_ice_blue)
        composeTestRule.onNodeWithText(iceBlue).performClick()

        // Assert
        assertEquals(0xFF90CAF9.toInt(), selectedColor)
    }

    @Test
    fun `when clock background is enabled, should display background color options`() {
        // Arrange
        var selectedBgColor = -1
        val actions = createDefaultActions {
            onUpdateClockBackgroundColor = { selectedBgColor = it }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBackgroundSection(
                    uiState = MainUiState(isClockBackgroundEnabled = true),
                    actions = actions
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Custom Background Color:").assertIsDisplayed()

        // Act - click "Escuro Slate"
        composeTestRule.onNodeWithText("Escuro Slate").performClick()

        // Assert
        assertEquals(0xFF1E2124.toInt(), selectedBgColor)
    }

    @Test
    fun `when toggling clock background switch, should invoke onToggleClockBackground`() {
        // Arrange
        var toggled = false
        val actions = createDefaultActions {
            onToggleClockBackground = { toggled = true }
        }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBackgroundSection(
                    uiState = MainUiState(isClockBackgroundEnabled = false),
                    actions = actions
                )
            }
        }

        // Act - click the toggleable Switch
        composeTestRule.onNode(isToggleable()).performClick()

        // Assert
        assertTrue(toggled)
    }

    @Test
    fun `when selecting a style card, should invoke onUpdateClockStyle with its key`() {
        var selectedStyle = ""
        val actions = createDefaultActions { onUpdateClockStyle = { selectedStyle = it } }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockStyleSectionHost(actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Time only").performClick()
        assertEquals("TIME_ONLY", selectedStyle)
        composeTestRule.onNodeWithText("Greeting and date").performClick()
        assertEquals("GREETING_AND_DATE", selectedStyle)
    }

    @androidx.compose.runtime.Composable
    private fun EditClockStyleSectionHost(actions: HomeScreenActions) {
        EditClockStyleSection(
            uiState = MainUiState(clockStyle = "DATE_ON_TOP"),
            actions = actions,
            tempCustomGreeting = "",
            onGreetingChange = {}
        )
    }
}
