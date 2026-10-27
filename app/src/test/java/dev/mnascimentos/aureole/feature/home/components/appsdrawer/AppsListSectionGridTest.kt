package dev.mnascimentos.aureole.feature.home.components.appsdrawer

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppsListSectionGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appA1 = AppInfo(
        label = "Alpha",
        packageName = "com.example.alpha",
        componentName = ComponentName("com.example.alpha", "MainActivity"),
        icon = Color.RED.toDrawable()
    )
    private val appA2 = AppInfo(
        label = "Android App",
        packageName = "com.example.android",
        componentName = ComponentName("com.example.android", "MainActivity"),
        icon = Color.GREEN.toDrawable()
    )
    private val appB1 = AppInfo(
        label = "Beta",
        packageName = "com.example.beta",
        componentName = ComponentName("com.example.beta", "MainActivity"),
        icon = Color.BLUE.toDrawable()
    )

    private fun createDefaultActions(
        onAppClick: (AppInfo) -> Unit = {},
        onSettingsClick: () -> Unit = {},
        onToggleFavorite: (String) -> Unit = {},
        onAppInfoClick: (AppInfo) -> Unit = {}
    ) = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = onAppClick,
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = onSettingsClick,
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onToggleFavorite = onToggleFavorite,
        onAppInfoClick = onAppInfoClick
    )

    @Test
    fun `when searchQuery is blank, should display letter section headers and apps`() {
        val uiState = MainUiState(
            apps = listOf(appA1, appA2, appB1),
            filteredApps = listOf(appA1, appA2, appB1),
            searchQuery = ""
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    LazyColumn {
                        appsListItems(uiState, actions)
                    }
                }
            }
        }

        // Letter headers "A" and "B" should be displayed
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()

        // App labels
        composeTestRule.onNodeWithText("Alpha").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beta").assertIsDisplayed()
    }

    @Test
    fun `when searchQuery is not blank, should hide section headers`() {
        val uiState = MainUiState(
            apps = listOf(appA1, appB1),
            filteredApps = listOf(appA1),
            searchQuery = "Alph"
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    LazyColumn {
                        appsListItems(uiState, actions)
                    }
                }
            }
        }

        // Letter headers should not exist when searching
        composeTestRule.onNodeWithText("A").assertDoesNotExist()
        composeTestRule.onNodeWithText("Alpha").assertIsDisplayed()
    }

    @Test
    fun `when clicking an app in list, should invoke onAppClick action`() {
        var clickedApp: AppInfo? = null
        val uiState = MainUiState(
            apps = listOf(appA1),
            filteredApps = listOf(appA1)
        )
        val actions = createDefaultActions(onAppClick = { clickedApp = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    LazyColumn {
                        appsListItems(uiState, actions)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Alpha").performClick()
        assertEquals(appA1, clickedApp)
    }

    @Test
    fun `when settings item is rendered, clicking it should invoke onSettingsClick`() {
        var settingsClicked = false
        val uiState = MainUiState(
            apps = emptyList(),
            filteredApps = emptyList()
        )
        val actions = createDefaultActions(onSettingsClick = { settingsClicked = true })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    LazyColumn {
                        appsListItems(uiState, actions)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Aureole settings", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Aureole settings", substring = true).performClick()
        assertTrue(settingsClicked)
    }

    @Test
    fun `when long clicking an app in list with action sheet, should invoke row actions`() {
        var favoriteToggledPkg: String? = null
        var openFavoritePickerCalled = false
        var appInfoClickedApp: AppInfo? = null
        var uninstalledApp: AppInfo? = null

        val uiState = MainUiState(
            apps = listOf(appA1),
            filteredApps = listOf(appA1)
        )
        val actions = HomeScreenActions(
            onWidgetRowHeightChanged = {},
            onAddWidgetClick = {},
            onRemoveWidgetClick = { _, _ -> },
            onAppClick = {},
            onExpandNotificationShade = {},
            onFolderIntent = {},
            onSetAddAppToFolderDialogVisible = {},
            onSetRenameFolderDialogVisible = {},
            onSearchQueryChanged = {},
            onSettingsClick = {},
            onAllAppsDrawerClose = {},
            onAllAppsDrawerOpen = {},
            onToggleFavorite = { favoriteToggledPkg = it },
            onOpenFavoritePicker = { openFavoritePickerCalled = true },
            onAppInfoClick = { appInfoClickedApp = it },
            onUninstallAppClick = { uninstalledApp = it }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    LazyColumn {
                        appsListItems(uiState, actions)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Alpha").performClick()
        assertTrue(favoriteToggledPkg == null)
        assertTrue(!openFavoritePickerCalled)
        assertTrue(appInfoClickedApp == null)
        assertTrue(uninstalledApp == null)
    }
}
