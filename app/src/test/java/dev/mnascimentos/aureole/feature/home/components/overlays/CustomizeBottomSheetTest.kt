package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
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
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(uiState = uiState, actions = actions, onDismissRequest = onDismiss)
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
    fun `when favorites is clicked, should ask for a favorites list and dismiss`() {
        var asked = false
        var dismissed = false
        show(
            createDefaultHomeScreenActions().copy(onAddAppsList = { asked = true }),
            onDismiss = { dismissed = true }
        )

        composeTestRule.onNodeWithText("Favorites").performScrollTo().performClick()
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
    fun `when all apps is clicked, should add an apps list and dismiss`() =
        assertAddsType("All apps", LauncherItemType.APPS_LIST)

    @Test
    fun `when container is clicked, should add a shortcuts container and dismiss`() =
        assertAddsType("Container", LauncherItemType.SHORTCUTS_CONTAINER)

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

        composeTestRule.onNodeWithText("Pick Font").assertExists()
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

    private companion object {
        const val DISMISS_WAIT_MS = 500L
        val LABELS_IN_ORDER = listOf(
            "Clock",
            "Favorites",
            "All apps",
            "Container",
            "Widget group",
            "Scrolling group",
            "Quick button"
        )
    }
}
