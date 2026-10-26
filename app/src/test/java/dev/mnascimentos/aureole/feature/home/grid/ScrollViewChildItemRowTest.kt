package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.ScrollViewChildParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import org.junit.Assert.assertEquals
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import dev.mnascimentos.aureole.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScrollViewChildItemRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockActions(
        onResizeChild: (String, String, Int, Int) -> Unit = { _, _, _, _ -> },
        onMoveChild: (String, String, Boolean) -> Unit = { _, _, _ -> }
    ) = HomeScreenActions(
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
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {},
        onResizeChildInScrollView = onResizeChild,
        onMoveChildInScrollView = onMoveChild
    )

    @Test
    fun `when rendering child item card, should display type title and span`() {
        val child = LauncherItemState(
            id = "c1",
            type = LauncherItemType.APPS_LIST,
            colSpan = 3,
            rowSpan = 2,
            col = 0,
            row = 0
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ScrollViewChildItemCard(
                    params = ScrollViewChildParams(
                        parentId = "p1",
                        child = child,
                        childIndex = 0,
                        totalChildren = 1,
                        isVertical = true
                    ),
                    actions = mockActions(),
                    onDismissRequest = {}
                )
            }
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        composeTestRule.onNodeWithText(context.getString(R.string.home_container_add_apps_list)).assertExists()
        composeTestRule.onNodeWithText("2x").assertExists() // vertical -> rowSpan
    }

    @Test
    fun `when clicking span adjust buttons, should call onResizeChildInScrollView`() {
        var resized = false
        val child = LauncherItemState(
            id = "c1",
            type = LauncherItemType.CLOCK,
            colSpan = 2,
            rowSpan = 2,
            col = 0,
            row = 0
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ScrollViewChildItemCard(
                    params = ScrollViewChildParams(
                        parentId = "p1",
                        child = child,
                        childIndex = 0,
                        totalChildren = 1,
                        isVertical = false
                    ),
                    actions = mockActions(
                        onResizeChild = { parent, childId, newCol, _ ->
                            assertEquals("p1", parent)
                            assertEquals("c1", childId)
                            assertEquals(3, newCol) // 2 + 1
                            resized = true
                        }
                    ),
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("2x").assertExists() // horizontal -> colSpan
        composeTestRule.onNodeWithText("+").performClick()
        assertTrue(resized)
    }
}
