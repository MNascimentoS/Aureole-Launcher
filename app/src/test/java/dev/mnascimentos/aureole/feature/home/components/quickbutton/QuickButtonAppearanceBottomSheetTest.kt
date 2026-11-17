package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickButtonAppearanceBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering appearance bottom sheet with open app shortcut, should resolve active app info`() {
        // Arrange
        val appInfo = AppInfo(
            label = "Gallery",
            packageName = "com.example.gallery",
            componentName = ComponentName("com.example.gallery", "MainActivity"),
            icon = ColorDrawable(Color.GREEN)
        )
        val model = QuickButtonModel(
            shortcuts = QuickButtonShortcuts(
                click = ShortcutAction.openApp("com.example.gallery")
            )
        )
        val uiState = MainUiState(
            activeQuickButtonModel = model,
            apps = listOf(appInfo),
            isHazeEnabled = true
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Colors").performClick()
        composeTestRule.onNodeWithText("Background color").assertIsDisplayed()
    }

    private fun setAppearanceSheet(actions: HomeScreenActions, dismissed: MutableList<Unit> = mutableListOf()) {
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = false))
        )
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = { dismissed += Unit }
                )
            }
        }
    }

    @Test
    fun `when toggling the stroke, should not write the button until the sheet closes`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithText("Colors").performClick()
        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.onNodeWithText("STROKE COLOR").assertExists()
        assertNull(saved)

        composeTestRule.closeOverlayWithBack()
        assertTrue(saved!!.strokeEnabled)
    }

    @Test
    fun `when closing after editing, should save the draft once and close`() {
        var saved: QuickButtonAppearance? = null
        val dismissed = mutableListOf<Unit>()
        setAppearanceSheet(
            createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }),
            dismissed
        )

        composeTestRule.onNodeWithText("Colors").performClick()
        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.closeOverlayWithBack()

        assertTrue(saved!!.strokeEnabled)
        assertEquals(1, dismissed.size)
    }

    @Test
    fun `when an icon of the catalog is tapped, should save it when the sheet closes and not before`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onAllNodes(isSelectable())[AureoleIconCatalog.categories.size + 1].performClick()
        assertNull(saved)

        composeTestRule.closeOverlayWithBack()
        assertNotEquals(QuickButtonAppearance().icon, saved!!.icon)
    }

    @Test
    fun `when a name is typed, should save it trimmed when the sheet closes`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithContentDescription("Name, Name it, edit").performTextInput("  Mail ")
        assertNull(saved)

        composeTestRule.closeOverlayWithBack()
        assertEquals("Mail", saved!!.label)
    }

    @Test
    fun `when nothing changes, should not save and should close`() {
        var saved: QuickButtonAppearance? = null
        val dismissed = mutableListOf<Unit>()
        setAppearanceSheet(
            createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }),
            dismissed
        )

        composeTestRule.closeOverlayWithBack()

        assertNull(saved)
        assertEquals(1, dismissed.size)
    }

    @Test
    fun `when icon and background have a low contrast, should warn in the preview`() {
        setSheetWith(QuickButtonAppearance(iconColor = ThemeColorRef.Fixed(1), backgroundColor = ThemeColorRef.Fixed(1)))

        composeTestRule.onNodeWithText("Low contrast: the icon may be hard to see").assertIsDisplayed()
    }

    @Test
    fun `when the colors contrast, should not warn`() {
        setSheetWith(QuickButtonAppearance())

        composeTestRule.onNodeWithText("Low contrast: the icon may be hard to see").assertDoesNotExist()
    }

    @Test
    fun `when Show name is on, should draw the name under the button, else not`() {
        setSheetWith(QuickButtonAppearance(label = "Mail", showLabel = true))
        // One in the name pill, one under the button.
        composeTestRule.onAllNodesWithText("Mail").assertCountEquals(2)
    }

    @Test
    fun `when Show name is on without a name, should draw the name of the click action`() {
        setSheetWith(QuickButtonAppearance(showLabel = true))
        composeTestRule.onNodeWithText("All apps").assertIsDisplayed()
    }

    @Test
    fun `when Show name is off, should not draw the name under the button`() {
        setSheetWith(QuickButtonAppearance(label = "Mail", showLabel = false))
        // Only the name pill has it.
        composeTestRule.onAllNodesWithText("Mail").assertCountEquals(1)
    }

    private fun setSheetWith(appearance: QuickButtonAppearance) {
        val uiState = MainUiState(activeQuickButtonModel = QuickButtonModel(appearance = appearance))
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }
    }
}
