package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import org.junit.Rule
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import dev.mnascimentos.aureole.R
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
       val context = ApplicationProvider.getApplicationContext<Context>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditDialogDeleteRow(
                    itemId = "item_1",
                    onDeleteConfirm = {},
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_remove_component)).assertIsDisplayed()
    }

    @Test
    fun `when rendering EditDialogTypeActionSection for CLOCK, should display personalize clock action`() {
       val context = ApplicationProvider.getApplicationContext<Context>()
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

        composeTestRule.onNodeWithText(context.getString(R.string.home_grid_customize_clock)).assertIsDisplayed()
    }

    @Test
    fun `when rendering EditDialogScrollViewSection, should display children section header`() {
       val context = ApplicationProvider.getApplicationContext<Context>()
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

        composeTestRule.onNodeWithText(context.getString(R.string.home_container_edit_child_components, 0)).assertIsDisplayed()
    }
}
