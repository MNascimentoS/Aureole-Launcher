package dev.mnascimentos.aureole.feature.home.grid

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
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
class EditGridItemDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun baseActions() = HomeScreenActions(
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

    @Test
    fun `when rendering dialog, should display current size`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val item = LauncherItemState(
            id = "test",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 3,
            rowSpan = 4
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides baseActions()
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_current_size, 3, 4)).assertExists()
    }

    @Test
    fun `when rendering clock, should show edit action and trigger callback`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var actionTriggered = false
        val item = LauncherItemState(
            id = "clock",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides baseActions().copy(
                        onOpenEditClockBottomSheet = { actionTriggered = true }
                    )
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_customize_clock)).performClick()
        assertTrue(actionTriggered)
    }

    @Test
    fun `when rendering scroll view, should show orientation options and trigger add child`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var orientationTriggered: ScrollOrientation? = null
        var addContainerTriggered = false
        val item = LauncherItemState(
            id = "sv",
            type = LauncherItemType.SCROLL_VIEW,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2,
            scrollOrientation = ScrollOrientation.VERTICAL
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides baseActions().copy(
                        onUpdateScrollViewOrientation = { _, o -> orientationTriggered = o },
                        onOpenAddContainerForParent = { _ -> addContainerTriggered = true }
                    )
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.home_container_edit_scroll_orientation)
        ).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.home_container_edit_horizontal)).performClick()
        assertEquals(ScrollOrientation.HORIZONTAL, orientationTriggered)

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_add_component)).performClick()
        assertTrue(addContainerTriggered)
    }

    @Test
    fun `when deleting item, should call onDeleteConfirm`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var deletedId: String? = null
        val item = LauncherItemState(
            id = "del_item",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides baseActions()
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = { deletedId = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_remove_component)).performClick()
        assertEquals("del_item", deletedId)
    }
}
