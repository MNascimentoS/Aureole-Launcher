package dev.mnascimentos.aureole.feature.home.components.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
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
class GridItemContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createDefaultActions() = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

    private fun createGridParams(
        type: LauncherItemType,
        widgetId: Int? = null,
        isInScrollView: Boolean = false
    ): GridItemContentParams {
        return GridItemContentParams(
            item = LauncherItemState(
                id = "item_1",
                type = type,
                widgetId = widgetId
            ),
            favConfig = FavoritesListConfig(
                currentHeightDp = 200.dp,
                currentHeightPx = 200f,
                state = LazyListState()
            ),
            appWidgetHost = null,
            stackedWidgetConfig = StackedWidgetConfig(
                topWidgetIds = emptyList(),
                currentHeightDp = 200.dp,
                currentHeightPx = 200f
            ),
            containerConfig = ContainerConfig(
                panelId = "p1",
                showFolderLabels = true
            ),
            isInScrollView = isInScrollView
        )
    }

    @Test
    fun `when rendering GridItemContent with CLOCK type, should render clock content`() {
        val params = createGridParams(LauncherItemType.CLOCK)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with APPS_LIST type outside scroll view, should render favorites list`() {
        val params = createGridParams(LauncherItemType.APPS_LIST, isInScrollView = false)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with APPS_LIST type inside scroll view, should render favorites list`() {
        val params = createGridParams(LauncherItemType.APPS_LIST, isInScrollView = true)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with SHORTCUTS_CONTAINER type, should render container`() {
        val params = createGridParams(LauncherItemType.SHORTCUTS_CONTAINER)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with WIDGET_LIST type outside scroll view, should render widget list`() {
        val params = createGridParams(LauncherItemType.WIDGET_LIST, isInScrollView = false)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with WIDGET_LIST type inside scroll view, should render widget list`() {
        val params = createGridParams(LauncherItemType.WIDGET_LIST, isInScrollView = true)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with QUICK_BUTTON type, should render quick button`() {
        val params = createGridParams(LauncherItemType.QUICK_BUTTON)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering GridItemContent with SCROLL_VIEW type, should render scroll view content`() {
        val params = createGridParams(LauncherItemType.SCROLL_VIEW)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when rendering SINGLE_APP_WIDGET without widgetId, should render unconfigured placeholder`() {
        val params = createGridParams(LauncherItemType.SINGLE_APP_WIDGET, widgetId = null)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText("Widget não configurado").assertIsDisplayed()
    }

    @Test
    fun `when rendering SINGLE_APP_WIDGET with invalid widgetId minus one, should render unconfigured placeholder`() {
        val params = createGridParams(LauncherItemType.SINGLE_APP_WIDGET, widgetId = -1)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText("Widget não configurado").assertIsDisplayed()
    }

    @Test
    fun `when valid widgetId and null host, should render single app widget view`() {
        val params = createGridParams(LauncherItemType.SINGLE_APP_WIDGET, widgetId = 101)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    GridItemContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when folder clicked in GridContainerContent and folder is not opened, should open folder`() {
        var folderIntentReceived: FolderViewIntent? = null
        val actions = createDefaultActions().copy(
            onFolderIntent = { intent -> folderIntentReceived = intent }
        )
        val folder = AppFolder(id = "f1", name = "Folder 1")
        val containerConfig = ContainerConfig(
            panelId = "p1",
            folders = listOf(folder),
            showFolderLabels = true
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(openedFolderId = null),
                    LocalHomeActions provides actions
                ) {
                    GridContainerContent(
                        containerConfig = containerConfig,
                        uiState = MainUiState(openedFolderId = null),
                        actions = actions
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder 1").performClick()
        assertTrue(folderIntentReceived is FolderViewIntent.OpenFolder)
        assertEquals("f1", (folderIntentReceived as FolderViewIntent.OpenFolder).folderId)
    }

    @Test
    fun `when folder clicked in GridContainerContent and folder is already opened, should close folder`() {
        var folderIntentReceived: FolderViewIntent? = null
        val actions = createDefaultActions().copy(
            onFolderIntent = { intent -> folderIntentReceived = intent }
        )
        val folder = AppFolder(id = "f1", name = "Folder 1")
        val containerConfig = ContainerConfig(
            panelId = "p1",
            folders = listOf(folder),
            showFolderLabels = true
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(openedFolderId = "f1"),
                    LocalHomeActions provides actions
                ) {
                    GridContainerContent(
                        containerConfig = containerConfig,
                        uiState = MainUiState(openedFolderId = "f1"),
                        actions = actions
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder 1").performClick()
        assertEquals(FolderViewIntent.CloseFolder, folderIntentReceived)
    }
}
