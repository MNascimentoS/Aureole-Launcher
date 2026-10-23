package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickButtonGridContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when item is tapped with all apps shortcut, should trigger onAllAppsDrawerOpen`() {
        // Arrange
        var drawerOpened = false
        val actions = createDefaultHomeScreenActions().copy(
            onAllAppsDrawerOpen = { drawerOpened = true }
        )
        val model = QuickButtonModel(
            id = "qb1",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.AllApps)
        )
        val item = LauncherItemState(id = "qb1", quickButtonModel = model)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = actions
                )
            }
        }

        // Assert
        composeTestRule.onRoot().performClick()
        assertTrue("Expected onAllAppsDrawerOpen to be called", drawerOpened)
    }

    @Test
    fun `when item is tapped with settings shortcut, should trigger onSettingsClick`() {
        // Arrange
        var settingsOpened = false
        val actions = createDefaultHomeScreenActions().copy(
            onSettingsClick = { settingsOpened = true }
        )
        val model = QuickButtonModel(
            id = "qb1",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.Settings)
        )
        val item = LauncherItemState(id = "qb1", quickButtonModel = model)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = actions
                )
            }
        }

        // Assert
        composeTestRule.onRoot().performClick()
        assertTrue("Expected onSettingsClick to be called", settingsOpened)
    }

    @Test
    fun `when item is long pressed, should trigger onOpenQuickButtonSettings`() {
        // Arrange
        var openedId: String? = null
        val actions = createDefaultHomeScreenActions().copy(
            onOpenQuickButtonSettings = { id -> openedId = id }
        )
        val model = QuickButtonModel(id = "qb_long_press")
        val item = LauncherItemState(id = "qb_long_press", quickButtonModel = model)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = actions
                )
            }
        }

        // Assert
        composeTestRule.onRoot().performTouchInput { longClick() }
        assertEquals("qb_long_press", openedId)
    }

    @Test
    fun `when item is open app shortcut and app exists in state, should display app icon`() {
        // Arrange
        val appInfo = AppInfo(
            label = "Maps",
            packageName = "com.google.android.apps.maps",
            componentName = ComponentName("com.google.android.apps.maps", "MapsActivity"),
            icon = ColorDrawable(Color.YELLOW)
        )
        val model = QuickButtonModel(
            id = "qb1",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.openApp("com.google.android.apps.maps")),
            appearance = QuickButtonAppearance(strokeEnabled = true)
        )
        val item = LauncherItemState(id = "qb1", quickButtonModel = model)
        val uiState = MainUiState(apps = listOf(appInfo))

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    GridQuickButtonContent(
                        item = item,
                        actions = createDefaultHomeScreenActions()
                    )
                }
            }
        }

        // Assert
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun `when item is open app shortcut with empty target, should handle tap gracefully`() {
        // Arrange
        val model = QuickButtonModel(
            id = "qb_empty_app",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.openApp(""))
        )
        val item = LauncherItemState(id = "qb_empty_app", quickButtonModel = model)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = createDefaultHomeScreenActions()
                )
            }
        }

        composeTestRule.onRoot().performClick()
    }

    @Test
    fun `when item is assistant shortcut, should attempt voice assistant launch`() {
        // Arrange
        val model = QuickButtonModel(
            id = "qb_assistant",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.Assistant)
        )
        val item = LauncherItemState(id = "qb_assistant", quickButtonModel = model)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = createDefaultHomeScreenActions()
                )
            }
        }

        composeTestRule.onRoot().performClick()
    }

    @Test
    fun `when item is notifications shortcut, should attempt notifications shade expansion`() {
        // Arrange
        val model = QuickButtonModel(
            id = "qb_notif",
            shortcuts = QuickButtonShortcuts(click = ShortcutAction.Notifications)
        )
        val item = LauncherItemState(id = "qb_notif", quickButtonModel = model)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridQuickButtonContent(
                    item = item,
                    actions = createDefaultHomeScreenActions()
                )
            }
        }

        composeTestRule.onRoot().performClick()
    }
}
