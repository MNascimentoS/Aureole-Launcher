package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickButtonSettingsBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering quick button settings, should display title and rows`() {
        // Arrange
        val uiState = MainUiState(
            activeQuickButtonId = "qb1",
            activeQuickButtonModel = QuickButtonModel(id = "qb1")
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonSettingsBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Click shortcut").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove quick button").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when clicking appearance row, should trigger open quick button appearance action`() {
        // Arrange
        var appearanceOpened = false
        val actions = createDefaultHomeScreenActions().copy(
            onOpenQuickButtonAppearance = { appearanceOpened = true }
        )
        val uiState = MainUiState(
            activeQuickButtonId = "qb1",
            activeQuickButtonModel = QuickButtonModel(id = "qb1")
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonSettingsBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Appearance").performClick()
        assertTrue("Expected onOpenQuickButtonAppearance to be called", appearanceOpened)
    }

    @Test
    fun `when click shortcut is open app with app in state, should display app label in row`() {
        // Arrange
        var shortcutTypeSelected: String? = null
        val actions = createDefaultHomeScreenActions().copy(
            onOpenShortcutSelection = { type -> shortcutTypeSelected = type }
        )
        val app = AppInfo(
            label = "Test App",
            packageName = "com.example.test",
            componentName = ComponentName("com.example.test", "MainActivity"),
            icon = ColorDrawable(Color.RED)
        )
        val model = QuickButtonModel(
            id = "qb1",
            shortcuts = QuickButtonShortcuts(
                click = ShortcutAction.openApp("com.example.test")
            )
        )
        val uiState = MainUiState(
            activeQuickButtonId = "qb1",
            activeQuickButtonModel = model,
            apps = listOf(app)
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonSettingsBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Open App (Test App)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Click shortcut").performClick()
        assertEquals("click", shortcutTypeSelected)
    }

    @Test
    fun `when clicking remove button, should trigger delete grid item action`() {
        // Arrange
        var deletedId: String? = null
        val actions = createDefaultHomeScreenActions().copy(
            onDeleteGridItem = { id -> deletedId = id }
        )
        val uiState = MainUiState(
            activeQuickButtonId = "qb_delete_id",
            activeQuickButtonModel = QuickButtonModel(id = "qb_delete_id")
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonSettingsBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Remove quick button").performScrollTo().performClick()
        assertEquals("qb_delete_id", deletedId)
    }
}
