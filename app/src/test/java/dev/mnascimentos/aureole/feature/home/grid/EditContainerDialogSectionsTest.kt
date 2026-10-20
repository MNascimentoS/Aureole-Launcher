package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerDialogSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockHomeScreenActions() = HomeScreenActions(
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
    fun `when rendering EditDialogDeleteRow, should display remove component label`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogDeleteRow(
                    itemId = "item_123",
                    onDeleteConfirm = {},
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Remover Componente").assertIsDisplayed()
    }

    @Test
    fun `when rendering EditDialogTypeActionSection for CLOCK, should display personalize clock action`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogTypeActionSection(
                    item = LauncherItemState(
                        id = "clock_1",
                        type = LauncherItemType.CLOCK,
                        col = 0,
                        row = 0,
                        colSpan = 2,
                        rowSpan = 2
                    ),
                    actions = mockHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Personalizar Relógio").assertIsDisplayed()
    }

    @Test
    fun `when rendering EditDialogWidgetListSection, should display widget list title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogWidgetListSection(
                    item = LauncherItemState(
                        id = "widget_1",
                        type = LauncherItemType.WIDGET_LIST,
                        col = 0,
                        row = 0,
                        colSpan = 2,
                        rowSpan = 2
                    ),
                    uiState = MainUiState(),
                    actions = mockHomeScreenActions()
                )
            }
        }

        composeTestRule.onNodeWithText("Widgets no Grupo (0)").assertIsDisplayed()
    }

    @Test
    fun `when rendering EditDialogScrollViewSection, should display children section header`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogScrollViewSection(
                    item = LauncherItemState(
                        id = "scroll_1",
                        type = LauncherItemType.SCROLL_VIEW,
                        col = 0,
                        row = 0,
                        colSpan = 4,
                        rowSpan = 4
                    ),
                    actions = mockHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Componentes Filhos (0)").assertIsDisplayed()
    }
}
