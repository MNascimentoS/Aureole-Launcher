package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
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
class CustomizeBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun show(
        actions: HomeScreenActions = createDefaultHomeScreenActions(),
        uiState: MainUiState = MainUiState(),
        onDismiss: () -> Unit = {},
        isShowcase: Boolean = false
    ) {
        // The showcase runs on the frame clock: an auto-advancing clock would play all 3 s before the test starts.
        if (isShowcase) composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = onDismiss,
                    isShowcase = isShowcase
                )
            }
        }
    }

    @Test
    fun `when rendered, should show Adjust before Add to home and every item in order`() {
        show()

        composeTestRule.onNodeWithText("Customize").assertIsDisplayed()
        LABELS_IN_ORDER.forEach { composeTestRule.onNodeWithText(it).assertExists() }
        val adjust = composeTestRule.onNodeWithText("ADJUST").getUnclippedBoundsInRoot().top
        val add = composeTestRule.onNodeWithText("ADD TO HOME").getUnclippedBoundsInRoot().top
        val layout = composeTestRule.onNodeWithText("Layout").getUnclippedBoundsInRoot().top
        val clock = composeTestRule.onNodeWithText("Clock").getUnclippedBoundsInRoot().top
        assertTrue(adjust < layout && layout < add && add < clock)
    }

    @Test
    fun `when apps list is clicked, should ask for a new apps list and dismiss`() {
        var asked = false
        var dismissed = false
        show(
            createDefaultHomeScreenActions().copy(onAddAppsList = { asked = true }),
            onDismiss = { dismissed = true }
        )

        composeTestRule.onNodeWithText("Apps list").performScrollTo().performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_WAIT_MS)

        assertTrue(asked)
        assertTrue(dismissed)
    }

    private fun assertAddsType(label: String, expected: LauncherItemType) {
        val added = mutableListOf<LauncherItemType>()
        var dismissed = 0
        show(
            createDefaultHomeScreenActions().copy(onAddGridItem = { type, _, _ -> added += type }),
            onDismiss = { dismissed++ }
        )

        composeTestRule.onNodeWithText(label).performScrollTo().performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_WAIT_MS)

        assertEquals(listOf(expected), added)
        assertEquals(1, dismissed)
    }

    @Test
    fun `when container is clicked, should add a shortcuts container and dismiss`() =
        assertAddsType("App & folder group", LauncherItemType.SHORTCUTS_CONTAINER)

    @Test
    fun `when widget group is clicked, should add a widget list and dismiss`() =
        assertAddsType("Widget group", LauncherItemType.WIDGET_LIST)

    @Test
    fun `when scrolling group is clicked, should add a scroll view and dismiss`() =
        assertAddsType("Scrolling group", LauncherItemType.SCROLL_VIEW)

    @Test
    fun `when quick button is clicked, should add a quick button and dismiss`() =
        assertAddsType("Quick button", LauncherItemType.QUICK_BUTTON)

    @Test
    fun `when widget is clicked, should open the single widget picker`() {
        var addingSingle = false
        var pickerOpened = false
        show(
            createDefaultHomeScreenActions().copy(
                onSetIsAddingSingleWidget = { addingSingle = it },
                onAddWidgetClick = { pickerOpened = true }
            )
        )

        composeTestRule.onNodeWithText("Widget").performScrollTo().performClick()

        assertTrue(addingSingle)
        assertTrue(pickerOpened)
    }

    @Test
    fun `when settings is clicked, should open the Aureole settings and dismiss`() {
        var opened = 0
        var dismissed = 0
        show(
            createDefaultHomeScreenActions().copy(onSettingsClick = { opened++ }),
            onDismiss = { dismissed++ }
        )

        composeTestRule.onNodeWithText("Settings").performScrollTo().performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_WAIT_MS)

        assertEquals(1, opened)
        assertEquals(1, dismissed)
    }

    @Test
    fun `the header should not show the logo, the Add to home grid has the Settings tile`() {
        show(createDefaultHomeScreenActions())

        composeTestRule.onNodeWithContentDescription("Aureole settings").assertDoesNotExist()
    }

    @Test
    fun `when layout is clicked, should enter grid edit mode and dismiss`() {
        var editModeEntered = false
        var dismissed = false
        show(
            createDefaultHomeScreenActions().copy(onEnterGridEditMode = { editModeEntered = true }),
            onDismiss = { dismissed = true }
        )

        composeTestRule.onNodeWithText("Layout").performScrollTo().performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_WAIT_MS)

        assertTrue(editModeEntered)
        assertTrue(dismissed)
    }

    @Test
    fun `when palette is clicked, should open the palette picker in place of the sheet`() {
        show()

        composeTestRule.onNodeWithText("Palette").performClick()

        composeTestRule.onNodeWithText("Pick a palette").assertExists()
        composeTestRule.onNodeWithText("ADD TO HOME").assertDoesNotExist()
    }

    @Test
    fun `when font is clicked, should open the font picker in place of the sheet`() {
        show()

        composeTestRule.onNodeWithText("Font").performClick()

        composeTestRule.onNodeWithText("Pick font").assertExists()
        composeTestRule.onNodeWithText("ADD TO HOME").assertDoesNotExist()
    }

    @Test
    fun `when the clock is already on the home, should keep it tappable to add another clock`() {
        var added = 0
        val uiState = MainUiState(gridItems = listOf(LauncherItemState(id = "c", type = LauncherItemType.CLOCK)))
        show(createDefaultHomeScreenActions().copy(onAddClock = { added++ }), uiState)

        composeTestRule.onNodeWithText("Clock").performScrollTo().performClick()

        assertEquals(1, added)
    }

    @Test
    fun `when shown as a showcase, no tile should respond and the logo should not open the settings`() {
        val added = mutableListOf<LauncherItemType>()
        var settings = 0
        var editMode = false
        var dismissed = 0
        show(
            createDefaultHomeScreenActions().copy(
                onAddGridItem = { type, _, _ -> added += type },
                onSettingsClick = { settings++ },
                onEnterGridEditMode = { editMode = true }
            ),
            onDismiss = { dismissed++ },
            isShowcase = true
        )
        composeTestRule.mainClock.advanceTimeBy(OPEN_MS)

        composeTestRule.onNodeWithText("Layout").performClick()
        composeTestRule.onNodeWithText("Palette").performClick()
        composeTestRule.mainClock.advanceTimeBy(SHOWCASE_HALF_MS)

        composeTestRule.onNodeWithContentDescription("Aureole settings").assertDoesNotExist()
        composeTestRule.onNodeWithText("Pick a palette").assertDoesNotExist()
        assertTrue(added.isEmpty())
        assertEquals(0, settings)
        assertEquals(false, editMode)
        assertEquals(0, dismissed)
    }

    @Test
    fun `when the showcase time runs out, should close on its own once`() {
        var dismissed = 0
        show(onDismiss = { dismissed++ }, isShowcase = true)

        composeTestRule.mainClock.advanceTimeBy(SHOWCASE_HALF_MS)
        assertEquals(0, dismissed)

        composeTestRule.mainClock.advanceTimeBy(SHOWCASE_HALF_MS + DISMISS_WAIT_MS)
        assertEquals(1, dismissed)
    }

    @Test
    fun `when not a showcase, should stay open`() {
        var dismissed = 0
        show(onDismiss = { dismissed++ })
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.mainClock.advanceTimeBy(SHOWCASE_HALF_MS * 3)

        assertEquals(0, dismissed)
    }

    private companion object {
        const val SHOWCASE_HALF_MS = 1_500L
        const val OPEN_MS = 400L
        const val DISMISS_WAIT_MS = 500L
        val LABELS_IN_ORDER = listOf(
            "Clock",
            "Apps list",
            "App & folder group",
            "Widget group",
            "Scrolling group",
            "Quick button",
            "Settings"
        )
    }
}
