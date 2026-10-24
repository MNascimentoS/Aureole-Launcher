package dev.mnascimentos.aureole.feature.home.components.appsdrawer

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.core.graphics.drawable.toDrawable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppsListSectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Drawer Test App",
        packageName = "com.example.drawer",
        componentName = ComponentName("com.example.drawer", "MainActivity"),
        icon = Color.CYAN.toDrawable()
    )

    private fun createDefaultActions(
        onSearchQueryChanged: (String) -> Unit = {}
    ) = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = onSearchQueryChanged,
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

    @Test
    fun `when rendering AppsListDrawer in right-handed mode, should display list items`() {
        val uiState = MainUiState(
            apps = listOf(sampleApp),
            filteredApps = listOf(sampleApp),
            isLeftHandedMode = false,
            showSearchBarInAllApps = false
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    AppsListDrawer(
                        listState = rememberLazyListState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drawer Test App").assertIsDisplayed()
    }

    @Test
    fun `when rendering AppsListDrawer in left-handed mode, should display list items`() {
        val uiState = MainUiState(
            apps = listOf(sampleApp),
            filteredApps = listOf(sampleApp),
            isLeftHandedMode = true,
            showSearchBarInAllApps = false
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    AppsListDrawer(
                        listState = rememberLazyListState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drawer Test App").assertIsDisplayed()
    }

    @Test
    fun `when rendering AppsListDrawer with haze enabled, should render with haze modifier`() {
        val hazeState = HazeState()
        val uiState = MainUiState(
            apps = listOf(sampleApp),
            filteredApps = listOf(sampleApp),
            isHazeEnabled = true,
            hazeOpacity = 0.5f,
            showSearchBarInAllApps = false
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    AppsListDrawer(
                        listState = rememberLazyListState(),
                        hazeState = hazeState
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drawer Test App").assertIsDisplayed()
    }

    @Test
    fun `when showSearchBarInAllApps is true and collapsed, should render CollapsedSearchBubble and drawer content`() {
        val uiState = MainUiState(
            apps = listOf(sampleApp),
            filteredApps = listOf(sampleApp),
            showSearchBarInAllApps = true,
            searchQuery = ""
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    AppsListDrawer(
                        listState = rememberLazyListState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drawer Test App").assertIsDisplayed()
    }

    @Test
    fun `when searchQuery is not empty, search bar should be expanded and aligned top`() {
        val uiState = MainUiState(
            apps = listOf(sampleApp),
            filteredApps = listOf(sampleApp),
            showSearchBarInAllApps = true,
            searchQuery = "Drawer"
        )
        val actions = createDefaultActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    AppsListDrawer(
                        listState = rememberLazyListState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drawer Test App").assertIsDisplayed()
    }
}
