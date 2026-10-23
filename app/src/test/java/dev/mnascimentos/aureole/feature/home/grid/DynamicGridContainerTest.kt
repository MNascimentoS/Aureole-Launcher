package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicGridContainerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockHomeScreenActions(
        onSaveGridEditMode: () -> Unit = {}
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
        onSaveGridEditMode = onSaveGridEditMode
    )

    @Test
    fun `when rendering container in normal mode, should render items`() {
        val items = listOf(
            LauncherItemState(id = "c1", type = LauncherItemType.CLOCK, colSpan = 2, rowSpan = 2, col = 0, row = 0),
            LauncherItemState(id = "c2", type = LauncherItemType.APPS_LIST, colSpan = 2, rowSpan = 2, col = 2, row = 0)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    DynamicGridContainer(
                        items = items,
                        config = GridEditConfig(isEditMode = false),
                        actions = mockHomeScreenActions(),
                        modifier = Modifier.testTag("container")
                    ) { item ->
                        Text(text = "Item ${item.id}")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Item c1").assertExists()
        composeTestRule.onNodeWithText("Item c2").assertExists()
    }

    @Test
    fun `when rendering container in edit mode and tapped, should trigger onSaveGridEditMode`() {
        val items = listOf(
            LauncherItemState(id = "c1", type = LauncherItemType.CLOCK, colSpan = 2, rowSpan = 2, col = 0, row = 0)
        )

        var saveTriggered = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeActions provides mockHomeScreenActions(onSaveGridEditMode = { saveTriggered = true })
                ) {
                    DynamicGridContainer(
                        items = items,
                        config = GridEditConfig(isEditMode = true),
                        actions = mockHomeScreenActions(onSaveGridEditMode = { saveTriggered = true }),
                        modifier = Modifier.testTag("container")
                    ) { item ->
                        Text(text = "Item ${item.id}")
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("container").performClick()
        assertTrue(saveTriggered)
    }
}
