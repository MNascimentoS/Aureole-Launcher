package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.feature.home.components.clock.EditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Expanded, the font and palette pickers reach the ceiling of the overlay (RF-F1 to RF-F3, D-03), on a phone-sized
 * screen where the catalog is shorter than the ceiling.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class PickerSheetsExpandedHeightTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun expandAndMeasure(
        expand: () -> Unit = {
            composeTestRule.onNodeWithContentDescription(HANDLE).performCustomAccessibilityActionWithLabel("Expand")
        },
        handle: () -> SemanticsNodeInteraction = { composeTestRule.onNodeWithContentDescription(HANDLE) },
        sheet: @Composable () -> Unit
    ): Float {
        composeTestRule.setContent { AureoleLauncherTheme { sheet() } }
        composeTestRule.mainClock.advanceTimeBy(SETTLE_MS)
        composeTestRule.waitForIdle()
        expand()
        composeTestRule.mainClock.advanceTimeBy(SETTLE_MS)
        composeTestRule.waitForIdle()
        val root = composeTestRule.onRoot().getUnclippedBoundsInRoot()
        val sheetTop = handle().getUnclippedBoundsInRoot().top - SHEET_TOP_PADDING
        return (root.bottom - sheetTop).value / (root.bottom - root.top).value
    }

    @Composable
    private fun ClockFontSheet() {
        PickFontBottomSheet(
            selectedFontName = FONT_KEY_LAUNCHER,
            onFontSelected = {},
            onDismissRequest = {},
            options = ThemePickerSheetOptions(
                componentFont = ComponentFontOptions(
                    title = "Clock font",
                    subtitle = "Only for this clock",
                    sectionLabel = "Clock font",
                    sample = "16:16",
                    isBold = true,
                    onBoldChange = {},
                    preview = { Text("16:16", modifier = Modifier.height(PREVIEW_HEIGHT)) }
                )
            )
        )
    }

    @Test
    fun `the font picker of a component reaches the ceiling`() {
        val fraction = expandAndMeasure { ClockFontSheet() }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `dragging the handle of the font picker up reaches the ceiling`() {
        val fraction = expandAndMeasure(
            expand = {
                composeTestRule.onNodeWithContentDescription(HANDLE)
                    .performTouchInput { swipeUp(startY = top, endY = top - SWIPE_PX, durationMillis = SWIPE_MS) }
            }
        ) { ClockFontSheet() }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `pulling the font grid up at its top expands the sheet to the ceiling`() {
        val fraction = expandAndMeasure(
            expand = {
                composeTestRule.onNodeWithText("Roboto")
                    .performTouchInput {
                        swipeUp(
                            startY = centerY,
                            endY = centerY - SWIPE_PX,
                            durationMillis = SWIPE_MS
                        )
                    }
            }
        ) { ClockFontSheet() }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `the font picker opened from the clock sheet reaches the ceiling`() {
        val fraction = expandAndMeasure(
            expand = {
                composeTestRule.onNodeWithText("Change font").performScrollTo().performClick()
                composeTestRule.mainClock.advanceTimeBy(SETTLE_MS)
                composeTestRule.waitForIdle()
                // The picker is drawn over the clock sheet: its handle is the last one.
                composeTestRule.onAllNodesWithContentDescription(HANDLE).onLast()
                    .performCustomAccessibilityActionWithLabel("Expand")
            },
            handle = { composeTestRule.onAllNodesWithContentDescription(HANDLE).onLast() }
        ) {
            EditClockBottomSheet(
                uiState = MainUiState(),
                actions = createDefaultHomeScreenActions(),
                onDismissRequest = {}
            )
        }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `the launcher font picker reaches the ceiling`() {
        val fraction = expandAndMeasure {
            PickFontBottomSheet(selectedFontName = FONT_KEY_LAUNCHER, onFontSelected = {}, onDismissRequest = {})
        }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `the palette picker reaches the ceiling`() {
        val fraction = expandAndMeasure {
            PickPaletteBottomSheet(selectedPaletteName = "", onPaletteSelected = {}, onDismissRequest = {})
        }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    @Test
    fun `the palette picker in color mode reaches the ceiling`() {
        val fraction = expandAndMeasure {
            PickPaletteBottomSheet(
                color = ComponentColorOptions(
                    role = ComponentColorRole.TEXT,
                    title = "Text color",
                    subtitle = "Only for this clock",
                    selected = null,
                    themeColor = Color.White,
                    onSelect = {}
                ),
                onDismissRequest = {}
            )
        }

        assertEquals(EXPANDED_FRACTION, fraction, FRACTION_TOLERANCE)
    }

    private companion object {
        const val HANDLE = "Close"
        const val SETTLE_MS = 1_000L
        const val EXPANDED_FRACTION = 0.9f
        const val FRACTION_TOLERANCE = 0.01f
        val SHEET_TOP_PADDING = 4.dp
        val PREVIEW_HEIGHT = 96.dp
        const val SWIPE_PX = 900f
        const val SWIPE_MS = 400L
    }
}
