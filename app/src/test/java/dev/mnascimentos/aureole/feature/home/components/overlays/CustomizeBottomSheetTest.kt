package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
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

    @Test
    fun `when customize bottom sheet is rendered, title and options are displayed`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Customize").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add clock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add container").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add scrollview container").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add quick button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add widget group").assertIsDisplayed()
    }

    @Test
    fun `when add clock option is clicked, should invoke onAddGridItem for clock and dismiss`() {
        var addedType: LauncherItemType? = null
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onAddGridItem = { type, _, _ -> addedType = type }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add clock").performScrollTo().performClick()

        assertEquals(LauncherItemType.CLOCK, addedType)
        assertTrue(dismissed)
    }

    @Test
    fun `when add container option is clicked, should invoke onAddGridItem for shortcuts container and dismiss`() {
        var addedType: LauncherItemType? = null
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onAddGridItem = { type, _, _ -> addedType = type }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add container").performScrollTo().performClick()

        assertEquals(LauncherItemType.SHORTCUTS_CONTAINER, addedType)
        assertTrue(dismissed)
    }

    @Test
    fun `when add scrollview option is clicked, should invoke onAddGridItem for scrollview and dismiss`() {
        var addedType: LauncherItemType? = null
        var addedOrientation: ScrollOrientation? = null
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onAddGridItem = { type, _, orientation ->
                addedType = type
                addedOrientation = orientation
            }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add scrollview container").performScrollTo().performClick()

        assertEquals(LauncherItemType.SCROLL_VIEW, addedType)
        assertEquals(ScrollOrientation.VERTICAL, addedOrientation)
        assertTrue(dismissed)
    }

    @Test
    fun `when add quick button option is clicked, should invoke onAddGridItem for quick button and dismiss`() {
        var addedType: LauncherItemType? = null
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onAddGridItem = { type, _, _ -> addedType = type }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add quick button").performScrollTo().performClick()

        assertEquals(LauncherItemType.QUICK_BUTTON, addedType)
        assertTrue(dismissed)
    }

    @Test
    fun `when add widget group option is clicked, should invoke onAddGridItem for widget list and dismiss`() {
        var addedType: LauncherItemType? = null
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onAddGridItem = { type, _, _ -> addedType = type }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add widget group").performScrollTo().performClick()

        assertEquals(LauncherItemType.WIDGET_LIST, addedType)
        assertTrue(dismissed)
    }

    @Test
    fun `when adjust layout option is clicked, should invoke onEnterGridEditMode and dismiss`() {
        var editModeEntered = false
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onEnterGridEditMode = { editModeEntered = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Adjust layout").performScrollTo().performClick()

        assertTrue(editModeEntered)
        assertTrue(dismissed)
    }

    @Test
    fun `when set background option is clicked, should invoke onSettingsClick and dismiss`() {
        var settingsClicked = false
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onSettingsClick = { settingsClicked = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Set Background").performScrollTo().performClick()

        assertTrue(settingsClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when aureole settings option is clicked, should invoke onSettingsClick and dismiss`() {
        var settingsClickedCount = 0
        var dismissed = false

        val actions = createDefaultHomeScreenActions().copy(
            onSettingsClick = { settingsClickedCount++ }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CustomizeBottomSheet(
                    actions = actions,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Aureole settings").performScrollTo().performClick()

        assertEquals(1, settingsClickedCount)
        assertTrue(dismissed)
    }
}
