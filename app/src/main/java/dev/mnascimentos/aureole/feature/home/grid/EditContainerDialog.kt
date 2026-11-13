package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuListSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.ScrollGroup
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

@Composable
fun EditGridItemDialog(
    item: LauncherItemState,
    onDismissRequest: () -> Unit,
    onDeleteConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current
    val title = when (item.type) {
        LauncherItemType.CLOCK -> stringResource(R.string.home_grid_item_clock)
        LauncherItemType.APPS_LIST -> stringResource(R.string.home_grid_item_apps_list)
        LauncherItemType.SHORTCUTS_CONTAINER -> stringResource(R.string.home_grid_item_shortcuts)
        LauncherItemType.SINGLE_APP_WIDGET -> stringResource(R.string.home_grid_item_widget)
        LauncherItemType.WIDGET_LIST -> stringResource(R.string.home_grid_item_widget_group)
        LauncherItemType.SCROLL_VIEW -> stringResource(R.string.home_grid_item_scroll_view)
        LauncherItemType.QUICK_BUTTON -> stringResource(R.string.home_grid_item_quick_button)
        else -> stringResource(R.string.home_grid_item_container)
    }

    val sizeNote = stringResource(R.string.home_grid_current_size, item.colSpan, item.rowSpan)
    val sectionLabel = stringResource(R.string.home_grid_menu_section)
    val deleteParams = editDialogDeleteParams(
        itemId = item.id,
        onDeleteConfirm = onDeleteConfirm,
        onDismissRequest = onDismissRequest
    )
    AureoleMenuListSheet(
        title = title,
        onDismiss = onDismissRequest,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> GridItemTypeIcon(type = item.type, modifier = m) },
            onSettingsClick = {
                actions.onSettingsClick()
                onDismissRequest()
            }
        )
    ) {
        buildList {
            add(MenuEntry.Note(text = sizeNote))
            add(MenuEntry.Section(text = sectionLabel))
            if (item.type == LauncherItemType.SCROLL_VIEW) {
                val scrollSection = MenuEntry.Content {
                    EditDialogScrollViewSection(item = item, actions = actions, onDismissRequest = onDismissRequest)
                }
                add(scrollSection)
            }
            val typeSection = MenuEntry.Content {
                EditDialogTypeActionSection(item = item, actions = actions, onDismissRequest = onDismissRequest)
            }
            add(typeSection)
            add(MenuEntry.Row(deleteParams))
        }
    }
}

@Composable
private fun GridItemTypeIcon(type: LauncherItemType?, modifier: Modifier = Modifier) {
    val tint = AureoleDS.colors.onSurfaceMedium
    when (type) {
        LauncherItemType.CLOCK -> AureoleDS.icons.Calendar(modifier = modifier, tint = tint)
        LauncherItemType.APPS_LIST -> AureoleDS.icons.AllApps(modifier = modifier, tint = tint)
        LauncherItemType.SHORTCUTS_CONTAINER -> AureoleDS.icons.AppSelect(modifier = modifier, tint = tint)
        LauncherItemType.QUICK_BUTTON -> AureoleDS.icons.Click(modifier = modifier, tint = tint)
        LauncherItemType.SCROLL_VIEW -> AureoleDS.icons.ScrollGroup(modifier = modifier, tint = tint)
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SINGLE_APP_WIDGET -> AureoleDS.icons.MultipleView(modifier = modifier, tint = tint)
        else -> AureoleDS.icons.Layout(modifier = modifier, tint = tint)
    }
}

@AureolePreview
@Composable
private fun EditGridItemDialogPreview() {
    val sampleItem = LauncherItemState(
        id = "sample_item",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    AureoleLauncherTheme {
        EditGridItemDialog(
            item = sampleItem,
            onDismissRequest = {},
            onDeleteConfirm = {}
        )
    }
}
