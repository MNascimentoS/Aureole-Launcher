package dev.mnascimentos.aureole.feature.home.components.content

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenScrollContentTest {

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

    private fun createScrollViewParams(
        orientation: ScrollOrientation,
        children: List<LauncherItemState> = emptyList()
    ): ScrollViewContainerParams {
        val parentItem = LauncherItemState(
            id = "scroll_parent",
            type = LauncherItemType.SCROLL_VIEW,
            scrollOrientation = orientation,
            children = children
        )
        return ScrollViewContainerParams(
            item = parentItem,
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
                panelId = "p1"
            )
        )
    }

    @Test
    fun `when vertical scroll view in edit mode, should display add component button and handle click`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var addedParentId: String? = null
        val actions = createDefaultActions().copy(
            onOpenAddContainerForParent = { id -> addedParentId = id }
        )
        val child = LauncherItemState(id = "child_1", type = LauncherItemType.CLOCK, rowSpan = 2)
        val params = createScrollViewParams(ScrollOrientation.VERTICAL, listOf(child))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(isGridEditMode = true),
                    LocalHomeActions provides actions
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).performClick()
        assertEquals("scroll_parent", addedParentId)
    }

    @Test
    fun `when vertical scroll view not in edit mode, should not display add component button`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val child = LauncherItemState(id = "child_1", type = LauncherItemType.CLOCK)
        val params = createScrollViewParams(ScrollOrientation.VERTICAL, listOf(child))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(isGridEditMode = false),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).assertDoesNotExist()
    }

    @Test
    fun `when horizontal scroll view in edit mode, should display add component button and handle click`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var addedParentId: String? = null
        val actions = createDefaultActions().copy(
            onOpenAddContainerForParent = { id -> addedParentId = id }
        )
        val child = LauncherItemState(id = "child_1", type = LauncherItemType.QUICK_BUTTON, colSpan = 3)
        val params = createScrollViewParams(ScrollOrientation.HORIZONTAL, listOf(child))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(isGridEditMode = true),
                    LocalHomeActions provides actions
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).performClick()
        assertEquals("scroll_parent", addedParentId)
    }

    @Test
    fun `when horizontal scroll view not in edit mode, should not display add component button`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val child = LauncherItemState(id = "child_1", type = LauncherItemType.QUICK_BUTTON)
        val params = createScrollViewParams(ScrollOrientation.HORIZONTAL, listOf(child))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(isGridEditMode = false),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_scroll_component)).assertDoesNotExist()
    }

    @Test
    fun `when vertical scroll view has apps list child, should render apps list child`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appsListChild = LauncherItemState(id = "apps_child", type = LauncherItemType.APPS_LIST)
        val params = createScrollViewParams(ScrollOrientation.VERTICAL, listOf(appsListChild))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when vertical scroll view has custom rowSpan child, should render child content`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val clockChild = LauncherItemState(
            id = "clock_child",
            type = LauncherItemType.CLOCK,
            rowSpan = 3
        )
        val params = createScrollViewParams(ScrollOrientation.VERTICAL, listOf(clockChild))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }
    }

    @Test
    fun `when horizontal scroll view has custom colSpan child, should render child content`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val containerChild = LauncherItemState(
            id = "container_child",
            type = LauncherItemType.SHORTCUTS_CONTAINER,
            colSpan = 4
        )
        val params = createScrollViewParams(ScrollOrientation.HORIZONTAL, listOf(containerChild))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides createDefaultActions()
                ) {
                    ScrollViewContainerContent(params = params)
                }
            }
        }
    }
}
