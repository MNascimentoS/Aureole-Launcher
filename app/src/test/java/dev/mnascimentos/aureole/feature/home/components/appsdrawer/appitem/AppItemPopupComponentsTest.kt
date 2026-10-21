package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppItemPopupComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Popup App Test",
        packageName = "com.example.popup",
        componentName = ComponentName("com.example.popup", "MainActivity"),
        icon = Color.RED.toDrawable(),
    )

    @Test
    fun `when rendering AppItemPopup, should display app header label`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemPopup(
                    app = sampleApp,
                    isFavorite = false,
                    actions = AppItemRowActions(
                        onAppInfoClick = {},
                        onToggleFavorite = {},
                        onEditFavoritesClick = {}
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Popup App Test").assertIsDisplayed()
        composeTestRule.onNodeWithText("App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun `when clicking App button in popup, should invoke onAppInfoClick and dismiss`() {
        var appInfoClicked = false
        var dismissed = false

        val actions = AppItemRowActions(
            onAppInfoClick = { app ->
                assertEquals(sampleApp, app)
                appInfoClicked = true
            }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemPopup(
                    app = sampleApp,
                    isFavorite = false,
                    actions = actions,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("App").performClick()
        assertTrue(appInfoClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when isFavorite is false, should display Add button and toggle favorite on click`() {
        var toggledPkg: String? = null
        var dismissed = false

        val actions = AppItemRowActions(
            onToggleFavorite = { pkg ->
                toggledPkg = pkg
            }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemPopup(
                    app = sampleApp,
                    isFavorite = false,
                    actions = actions,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add").performClick()

        assertEquals(sampleApp.packageName, toggledPkg)
        assertTrue(dismissed)
    }

    @Test
    fun `when isFavorite is true, should display Remove button and toggle favorite on click`() {
        var toggledPkg: String? = null
        var dismissed = false

        val actions = AppItemRowActions(
            onToggleFavorite = { pkg ->
                toggledPkg = pkg
            }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemPopup(
                    app = sampleApp,
                    isFavorite = true,
                    actions = actions,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Remove").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove").performClick()

        assertEquals(sampleApp.packageName, toggledPkg)
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking Settings button, should invoke onEditFavoritesClick and dismiss`() {
        var settingsClicked = false
        var dismissed = false

        val actions = AppItemRowActions(
            onEditFavoritesClick = {
                settingsClicked = true
            }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemPopup(
                    app = sampleApp,
                    isFavorite = false,
                    actions = actions,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        assertTrue(settingsClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when actions are null, action buttons should not exist`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppPopupHorizontalActionsRow(
                    app = sampleApp,
                    isFavorite = false,
                    actions = AppItemRowActions(
                        onAppInfoClick = null,
                        onToggleFavorite = null,
                        onEditFavoritesClick = null
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("App").assertDoesNotExist()
        composeTestRule.onNodeWithText("Add").assertDoesNotExist()
        composeTestRule.onNodeWithText("Remove").assertDoesNotExist()
        composeTestRule.onNodeWithText("Settings").assertDoesNotExist()
    }
}
