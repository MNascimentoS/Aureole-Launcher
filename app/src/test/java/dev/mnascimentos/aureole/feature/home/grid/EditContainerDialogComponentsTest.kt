package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerDialogComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockActions(
        onOpenAddContainerForParent: (String) -> Unit = {}
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
        onOpenAddContainerForParent = onOpenAddContainerForParent
    )

    @Test
    fun `when rendering children list section empty, should show empty message`() {
        val item = LauncherItemState(id = "c1", type = LauncherItemType.SCROLL_VIEW)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogChildrenListSection(
                    item = item,
                    actions = mockActions(),
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nenhum componente adicionado ainda.").assertExists()
    }

    @Test
    fun `when rendering orientation selector, should trigger onUpdateScrollViewOrientation`() {
        var actionTriggered = false
        val item = LauncherItemState(id = "sv", type = LauncherItemType.SCROLL_VIEW)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ScrollViewOrientationSelector(
                    item = item,
                    actions = HomeScreenActions(
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
                        onUpdateScrollViewOrientation = { _, _ -> actionTriggered = true }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Horizontal").performClick()
        assertTrue(actionTriggered)
    }
}
