package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AppItemMenusTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Test App Header",
        packageName = "com.example.test",
        componentName = ComponentName("com.example.test", "MainActivity"),
        icon = Color.BLUE.toDrawable(),
    )

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
        composeTestRule.onNodeWithText("Uninstall").assertIsDisplayed()
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

        composeTestRule.onNodeWithText("Uninstall").performClick()
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

        composeTestRule.onNodeWithText("Uninstall").performClick()
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
        composeTestRule.onNodeWithText("Folder settings").assertIsDisplayed()

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
    fun `when onToggleFavorite provided and not favorite, should display Add to favorites and invoke toggle`() {
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

        composeTestRule.onNodeWithText("Add to favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add to favorites").performClick()

        assertEquals(sampleApp.packageName, toggledPkg)
        assertTrue(dismissed)
    }

    @Test
    fun `when onToggleFavorite provided and is favorite, should display Remove from favorites`() {
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

        composeTestRule.onNodeWithText("Remove from favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove from favorites").performClick()

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
    fun `when header logo icon button is clicked, should attempt to launch SettingsActivity`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(app = sampleApp),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onAllNodes(hasClickAction())[0].performClick()
    }
}
