package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicGridContainerEdgeCasesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockHomeScreenActions() = HomeScreenActions(
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
        onResizeGridItem = { _, _, _ -> },
        onOpenEditClockBottomSheet = {},
        onOpenFavoritePicker = {},
        onOpenEditContainerDialog = {},
        onOpenEditGridItemDialog = {},
        onDeleteGridItem = {}
    )

    @Test
    fun `when rendering container in edit mode, should display edit overlay correctly`() {
        val items = listOf(
            LauncherItemState(
                id = "sv",
                type = LauncherItemType.SCROLL_VIEW,
                colSpan = 2,
                rowSpan = 2,
                col = 0,
                row = 0
            ),
            LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, colSpan = 2, rowSpan = 2, col = 2, row = 0),
            LauncherItemState(
                id = "apps",
                type = LauncherItemType.APPS_LIST,
                colSpan = 2,
                rowSpan = 2,
                col = 0,
                row = 2
            ),
            LauncherItemState(
                id = "container",
                type = LauncherItemType.SHORTCUTS_CONTAINER,
                colSpan = 2,
                rowSpan = 2,
                col = 2,
                row = 2
            ),
            LauncherItemState(
                id = "other",
                type = LauncherItemType.SINGLE_APP_WIDGET,
                colSpan = 2,
                rowSpan = 2,
                col = 0,
                row = 4
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                DynamicGridContainer(
                    items = items,
                    config = GridEditConfig(isEditMode = true),
                    actions = mockHomeScreenActions(),
                    modifier = Modifier
                ) {}
            }
        }
    }
}
