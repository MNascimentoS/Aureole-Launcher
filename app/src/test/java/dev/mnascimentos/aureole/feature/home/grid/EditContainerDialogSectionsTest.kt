package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
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
    fun `when rendering EditDialogDeleteRow, should display text remover componente`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogDeleteRow(
                    itemId = "item_1",
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
