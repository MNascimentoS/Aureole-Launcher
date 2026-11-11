package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShortcutSelectionSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when default list is displayed, should render default options and apply the clicked one`() {
        // Arrange
        var appliedShortcut: ShortcutAction? = null
        val params = ShortcutSelectionSheetParams(
            category = ShortcutCategory.AUREOLE,
            currentAction = ShortcutAction.AllApps,
            defaultShortcutOptions = listOf(ShortcutAction.AllApps, ShortcutAction.Settings),
            uiState = MainUiState()
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionSheetContent(
                    params = params,
                    onApplyShortcut = { appliedShortcut = it }
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("All apps").assertIsDisplayed()
        composeTestRule.onNodeWithText("AUREOLE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aureole Settings").assertIsDisplayed().performClick()
        assertEquals(ShortcutAction.Settings, appliedShortcut)
    }

    @Test
    fun `when app selected in open app shortcut, should list the system shortcuts of the category`() {
        // Arrange
        val app = AppInfo(
            label = "Browser App",
            packageName = "com.example.browser",
            componentName = ComponentName("com.example.browser", "BrowserActivity"),
            icon = ColorDrawable(Color.BLACK)
        )
        val params = ShortcutSelectionSheetParams(
            category = ShortcutCategory.SYSTEM,
            currentAction = ShortcutAction.systemIntent("Camera"),
            defaultShortcutOptions = listOf(ShortcutAction.AllApps, ShortcutAction.systemIntent("Camera")),
            uiState = MainUiState(apps = listOf(app))
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionSheetContent(
                    params = params,
                    onApplyShortcut = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("All apps").assertDoesNotExist()
    }

    @Test
    fun `when app picker is true, should render app list and apply open app shortcut on click`() {
        // Arrange
        var appliedShortcut: ShortcutAction? = null
        val app1 = AppInfo(
            label = "Zebra App",
            packageName = "com.example.zebra",
            componentName = ComponentName("com.example.zebra", "MainActivity"),
            icon = ColorDrawable(Color.RED)
        )
        val app2 = AppInfo(
            label = "Alpha App",
            packageName = "com.example.alpha",
            componentName = ComponentName("com.example.alpha", "MainActivity"),
            icon = ColorDrawable(Color.BLUE)
        )
        val params = ShortcutSelectionSheetParams(
            category = ShortcutCategory.APPS,
            currentAction = ShortcutAction.None,
            defaultShortcutOptions = listOf(ShortcutAction.AllApps),
            uiState = MainUiState(apps = listOf(app1, app2))
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionSheetContent(
                    params = params,
                    onApplyShortcut = { appliedShortcut = it }
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Alpha App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zebra App").assertIsDisplayed().performClick()

        assertNotNull(appliedShortcut)
        assertEquals("com.example.zebra", appliedShortcut!!.target)
    }
}
