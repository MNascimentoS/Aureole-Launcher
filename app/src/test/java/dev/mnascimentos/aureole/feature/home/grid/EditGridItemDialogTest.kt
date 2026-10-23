package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    private fun mockHomeScreenActions(
        onOpenEditClockBottomSheet: () -> Unit = {},
        onOpenFavoritePicker: (String?) -> Unit = {},
        onOpenEditContainerDialog: (String) -> Unit = {},
        onOpenQuickButtonSettings: (String) -> Unit = {},
        onUpdateScrollViewOrientation: (String, ScrollOrientation) -> Unit = { _, _ -> },
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
        onOpenEditClockBottomSheet = onOpenEditClockBottomSheet,
        onOpenFavoritePicker = onOpenFavoritePicker,
        onOpenEditContainerDialog = onOpenEditContainerDialog,
        onOpenQuickButtonSettings = onOpenQuickButtonSettings,
        onUpdateScrollViewOrientation = onUpdateScrollViewOrientation,
        onOpenAddContainerForParent = onOpenAddContainerForParent,
    )

    @Test
    fun `when rendering dialog, should display current size`() {
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
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Tamanho atual: 3 x 4 células").assertExists()
    }

    @Test
    fun `when rendering clock, should show edit action and trigger callback`() {
        var actionTriggered = false
        val item =
            LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 2)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides mockHomeScreenActions(
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

        composeTestRule.onNodeWithText("Personalizar Relógio").performClick()
        assertTrue(actionTriggered)
    }

    @Test
    fun `when rendering scroll view, should show orientation options and trigger add child`() {
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
                    LocalHomeActions provides mockHomeScreenActions(
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

        composeTestRule.onNodeWithText("Orientação do Scroll").assertExists()
        composeTestRule.onNodeWithText("Horizontal").performClick()
        assertEquals(ScrollOrientation.HORIZONTAL, orientationTriggered)

        composeTestRule.onNodeWithText("Adicionar Componente").performClick()
        assertTrue(addContainerTriggered)
    }

    @Test
    fun `when deleting item, should call onDeleteConfirm`() {
        var deletedId: String? = null
        val item =
            LauncherItemState(
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
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    EditGridItemDialog(
                        item = item,
                        onDismissRequest = {},
                        onDeleteConfirm = { deletedId = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Remover Componente").performClick()
        assertEquals("del_item", deletedId)
    }
}
