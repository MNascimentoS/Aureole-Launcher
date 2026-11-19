package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        var onUpdateClockTextColor: (ThemeColorRef?) -> Unit = {},
        var onUpdateClockBackgroundColor: (ThemeColorRef?) -> Unit = {},
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
        composeTestRule.onNodeWithText("Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
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
    fun `when picking Theme default for the text, should invoke onUpdateClockTextColor with null`() {
        var picked: ThemeColorRef? = ThemeColorRef.Fixed(1)
        val actions = createDefaultActions { onUpdateClockTextColor = { picked = it } }
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockColorsSection(uiState = MainUiState(clockTextColor = 0xFF90CAF9.toInt()), actions = actions)
            }
        }

        tapSwatch(DEFAULT_SWATCH)

        assertNull(picked)
    }

    @Test
    fun `when picking a theme role for the text, should invoke onUpdateClockTextColor with that role`() {
        var picked: ThemeColorRef? = null
        val actions = createDefaultActions { onUpdateClockTextColor = { picked = it } }
        composeTestRule.setContent {
            AureoleLauncherTheme { EditClockColorsSection(uiState = MainUiState(), actions = actions) }
        }

        tapSwatch(DEFAULT_SWATCH + 1 + ThemeColorRole.ACCENT.ordinal)

        assertEquals(ThemeColorRef.Role(ThemeColorRole.ACCENT), picked)
    }

    @Test
    fun `when the card is on and the Card target is open, should pick its color`() {
        var picked: ThemeColorRef? = ThemeColorRef.Fixed(1)
        val actions = createDefaultActions { onUpdateClockBackgroundColor = { picked = it } }
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockColorsSection(uiState = MainUiState(isClockBackgroundEnabled = true), actions = actions)
            }
        }

        composeTestRule.onNodeWithText(string(R.string.home_clock_color_target_card)).performClick()
        tapSwatch(DEFAULT_SWATCH)

        assertNull(picked)
    }

    @Test
    fun `when the card is off and the Card target is open, should hide the picker and offer the switch`() {
        var toggled = false
        val actions = createDefaultActions { onToggleClockBackground = { toggled = true } }
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockColorsSection(uiState = MainUiState(isClockBackgroundEnabled = false), actions = actions)
            }
        }

        composeTestRule.onNodeWithText(string(R.string.home_clock_color_target_card)).performClick()
        composeTestRule.onNodeWithText(string(R.string.theme_color_swatch_default)).assertDoesNotExist()
        composeTestRule.onNode(isToggleable()).performClick()

        assertTrue(toggled)
    }

    /** Selectable nodes of the Colors tab: 2 targets (Text | Card), the default swatch, then the 5 theme roles. */
    private fun tapSwatch(index: Int) {
        composeTestRule.onAllNodes(isSelectable())[index].performClick()
    }

    private fun string(id: Int): String = ApplicationProvider.getApplicationContext<Context>().getString(id)

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
        composeTestRule.onNodeWithText("Info and date").performClick()
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

    private companion object {
        const val DEFAULT_SWATCH = 2
    }
}
