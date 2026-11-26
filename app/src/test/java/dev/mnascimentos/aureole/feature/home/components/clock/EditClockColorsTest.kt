package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.paletteNameOf
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
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

/** Colors tab without the card switch and the color page inside the clock sheet (RF-E1, E2, E4, E5). */
@RunWith(RobolectricTestRunner::class)
class EditClockColorsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(id: Int): String = ApplicationProvider.getApplicationContext<Context>().getString(id)

    private fun setSheet(uiState: MainUiState, actions: HomeScreenActions = createDefaultHomeScreenActions()) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockOverlay(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {},
                    options = EditClockOverlayOptions(
                        overlayState = AureoleEditOverlayState(initialExpanded = true),
                        initialTab = ClockTab.COLORS
                    )
                )
            }
        }
        advance()
    }

    private fun advance() {
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `the Colors rows name the saved colors, None for no card, and open the picker for their role`() {
        val opened = mutableListOf<ComponentColorRole>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockColorsSection(
                    uiState = MainUiState(clockTextColor = OLD_ARGB),
                    onOpenColorPicker = { opened += it }
                )
            }
        }

        composeTestRule.onNodeWithText(string(R.string.theme_color_option_current)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.home_clock_color_none)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.home_clock_color_text_title)).performClick()
        composeTestRule.onNodeWithText(string(R.string.home_clock_color_card_title)).performClick()

        assertEquals(listOf(ComponentColorRole.TEXT, ComponentColorRole.CARD), opened)
    }

    @Test
    fun `when the clock has the theme card, should name it Theme`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockColorsSection(uiState = MainUiState(clockBackgroundColorRef = CLOCK_CARD_THEME_REF.toJson()))
            }
        }

        assertEquals(
            2,
            composeTestRule.onAllNodes(hasText(string(R.string.theme_color_swatch_default))).fetchSemanticsNodes().size
        )
    }

    @Test
    fun `the card page opens in the sheet with None first, hides the tabs and goes back after a pick`() {
        var picked: ThemeColorRef? = null
        val actions = createDefaultHomeScreenActions().copy(onUpdateClockBackgroundColor = { picked = it })
        setSheet(MainUiState(), actions)

        composeTestRule.onNodeWithText(string(R.string.home_clock_color_card_title)).performClick()
        advance()

        composeTestRule.onNodeWithText(string(R.string.home_clock_picker_subtitle)).assertIsDisplayed()
        composeTestRule.onNode(hasText(string(R.string.home_clock_color_none)) and isSelectable()).assertIsSelected()
        composeTestRule.onNodeWithText("Format").assertDoesNotExist()
        composeTestRule.onNodeWithText(frostbitePalette.name).performClick()
        advance()

        assertEquals(frostbitePalette.name, paletteNameOf(picked, ComponentColorRole.CARD))
        composeTestRule.onNodeWithText("Format").assertIsDisplayed()
    }

    @Test
    fun `the card page saves None as null`() {
        var picked: ThemeColorRef? = CLOCK_CARD_THEME_REF
        val actions = createDefaultHomeScreenActions().copy(onUpdateClockBackgroundColor = { picked = it })
        setSheet(MainUiState(clockBackgroundColorRef = CLOCK_CARD_THEME_REF.toJson()), actions)

        composeTestRule.onNodeWithText(string(R.string.home_clock_color_card_title)).performClick()
        advance()
        composeTestRule.onNodeWithText(string(R.string.home_clock_color_none)).performClick()

        assertNull(picked)
    }

    @Test
    fun `the back arrow of the text page keeps the color and returns to the tabs`() {
        var calls = 0
        val actions = createDefaultHomeScreenActions().copy(onUpdateClockTextColor = { calls++ })
        setSheet(MainUiState(clockTextColor = OLD_ARGB), actions)

        composeTestRule.onNodeWithText(string(R.string.home_clock_color_text_title)).performClick()
        advance()
        val current = hasText(string(R.string.theme_color_option_current)) and isSelectable()
        composeTestRule.onNode(current).assertIsSelected()
        composeTestRule.onNodeWithContentDescription(string(R.string.overlay_sheet_page_back)).performClick()
        advance()

        assertEquals(0, calls)
        assertTrue(composeTestRule.onAllNodes(hasText("Format")).fetchSemanticsNodes().isNotEmpty())
    }

    private companion object {
        const val OLD_ARGB = 0xFF123457.toInt()
    }
}
