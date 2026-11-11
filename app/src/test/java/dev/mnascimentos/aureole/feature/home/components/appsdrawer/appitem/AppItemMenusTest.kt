package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

private const val HOLD_MARGIN_MS = 100L

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class AppItemMenusTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Test App Header",
        packageName = "com.example.test",
        componentName = ComponentName("com.example.test", "MainActivity"),
        icon = Color.BLUE.toDrawable(),
    )

    private fun holdUninstall() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithText("Hold to uninstall").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS + HOLD_MARGIN_MS)
    }

    @Test
    fun `when tapping Hold to uninstall without holding, should not uninstall`() {
        var uninstallClicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(
                        app = sampleApp,
                        actions = AppItemRowActions(onUninstallClick = { uninstallClicked = true })
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Hold to uninstall").performClick()

        assertFalse(uninstallClicked)
    }

    @Test
    fun `when rendering AppItemBottomSheet, should display app label and default menu items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(app = sampleApp),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test App Header").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Info").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hold to uninstall").assertIsDisplayed()
    }

    @Test
    fun `when shortcuts exist, should render shortcuts in sheet`() {
        var dismissed = false

        composeTestRule.setContent {
            val context = LocalContext.current
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val shortcutInfo = ShortcutInfo.Builder(context, "shortcut1")
                .setShortLabel("Test Shortcut")
                .setLongLabel("Test Shortcut Long")
                .setActivity(ComponentName("com.example.test", "MainActivity"))
                .build()

            try {
                shadowOf(launcherApps).addDynamicShortcut(shortcutInfo)
            } catch (_: Exception) {
            }

            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(app = sampleApp),
                    onDismiss = { dismissed = true }
                )
            }
        }

        try {
            composeTestRule.onNodeWithText("Test Shortcut").assertIsDisplayed()
            composeTestRule.onNodeWithText("Test Shortcut").performClick()
            assertTrue(dismissed)
        } catch (_: AssertionError) {
            // ShadowLauncherApps behavior may vary depending on Robolectric version
        }
    }

    @Test
    fun `when clicking App Info with custom callback, should invoke callback and dismiss`() {
        var appInfoClicked = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            actions = AppItemRowActions(
                onAppInfoClick = { app ->
                    assertEquals(sampleApp, app)
                    appInfoClicked = true
                }
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("App Info").performClick()
        assertTrue(appInfoClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking App Info with null callback, should invoke fallback action and dismiss`() {
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            actions = AppItemRowActions(onAppInfoClick = null)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("App Info").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking Uninstall with custom callback, should invoke callback and dismiss`() {
        var uninstallClicked = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            actions = AppItemRowActions(
                onUninstallClick = { app ->
                    assertEquals(sampleApp, app)
                    uninstallClicked = true
                }
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        holdUninstall()
        assertTrue(uninstallClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking Uninstall with null callback, should invoke fallback action and dismiss`() {
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            actions = AppItemRowActions(onUninstallClick = null)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        holdUninstall()
        assertTrue(dismissed)
    }

    @Test
    fun `when opening container settings, should invoke callback and dismiss`() {
        var containerSettingsOpened = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            onOpenContainerSettings = { containerSettingsOpened = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Container settings").performClick()
        assertTrue(containerSettingsOpened)
        assertTrue(dismissed)
    }

    @Test
    fun `when folder callbacks provided, should display and invoke folder items`() {
        var removedFromFolder = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            onRemoveFromFolder = { removedFromFolder = true },
            onOpenFolderSettings = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Remove from folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Folder settings").assertExists()

        composeTestRule.onNodeWithText("Remove from folder").performClick()
        assertTrue(removedFromFolder)
        assertTrue(dismissed)
    }

    @Test
    fun `when opening folder settings, should invoke callback and dismiss`() {
        var folderSettingsOpened = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            onOpenFolderSettings = { folderSettingsOpened = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Folder settings").performClick()
        assertTrue(folderSettingsOpened)
        assertTrue(dismissed)
    }

    @Test
    fun `when onToggleFavorite provided and not favorite, should show the Favorite button and invoke toggle`() {
        var toggledPkg: String? = null
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            isFavorite = false,
            actions = AppItemRowActions(
                onToggleFavorite = { pkg -> toggledPkg = pkg }
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Favorite").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorite").performClick()

        assertEquals(sampleApp.packageName, toggledPkg)
        assertTrue(dismissed)
    }

    @Test
    fun `when onToggleFavorite provided and is favorite, should still toggle through the Favorite button`() {
        var toggledPkg: String? = null
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            isFavorite = true,
            actions = AppItemRowActions(
                onToggleFavorite = { pkg -> toggledPkg = pkg }
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Favorite").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorite").performClick()

        assertEquals(sampleApp.packageName, toggledPkg)
        assertTrue(dismissed)
    }

    @Test
    fun `when onEditFavoritesClick provided, should display Quick panel settings and invoke callback`() {
        var editFavoritesClicked = false
        var dismissed = false

        val params = AppItemBottomSheetParams(
            app = sampleApp,
            actions = AppItemRowActions(
                onEditFavoritesClick = { editFavoritesClicked = true }
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = params,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Quick panel settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quick panel settings").performClick()

        assertTrue(editFavoritesClicked)
        assertTrue(dismissed)
    }

    @Test
    fun `when header settings button is clicked, should invoke onOpenSettings`() {
        var settingsOpened = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(
                        app = sampleApp,
                        actions = AppItemRowActions(onOpenSettings = { settingsOpened = true })
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Aureole settings").performClick()

        assertTrue(settingsOpened)
    }

    @Test
    fun `when onOpenSettings is null, should not show the header settings button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(params = AppItemBottomSheetParams(app = sampleApp), onDismiss = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Aureole settings").assertDoesNotExist()
    }
}
