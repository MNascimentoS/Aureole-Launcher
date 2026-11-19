package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Clock
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

/** What a row of a scroll group shows and does by item type: edit, icon and name (RF-C4, RF-C5). */

internal fun handleEditChildClick(
    child: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    when (child.type) {
        LauncherItemType.CLOCK -> {
            actions.onOpenEditClockBottomSheet(child.id)
            onDismissRequest()
        }
        LauncherItemType.SHORTCUTS_CONTAINER,
        LauncherItemType.SCROLL_VIEW -> {
            actions.onOpenEditContainerDialog(child.id)
            onDismissRequest()
        }
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SINGLE_APP_WIDGET -> {
            actions.onOpenWidgetStackBottomSheet(null, child.id)
            onDismissRequest()
        }
        LauncherItemType.APPS_LIST -> {
            actions.onOpenFavoritePicker(child.id)
            onDismissRequest()
        }
        LauncherItemType.QUICK_BUTTON -> {
            actions.onOpenQuickButtonSettings(child.id)
            onDismissRequest()
        }
        else -> {}
    }
}

@Composable
internal fun ChildTypeIcon(type: LauncherItemType?) {
    val iconModifier = Modifier.size(AureoleDS.dimens.iconMedium)
    when (type) {
        LauncherItemType.CLOCK -> AureoleDS.icons.Clock(iconModifier)
        LauncherItemType.APPS_LIST -> AureoleDS.icons.AllApps(iconModifier)
        LauncherItemType.SHORTCUTS_CONTAINER -> AureoleDS.icons.MenuIcon(iconModifier)
        LauncherItemType.WIDGET_LIST -> AureoleDS.icons.Add(iconModifier)
        LauncherItemType.SCROLL_VIEW -> AureoleDS.icons.VerticalContainer(iconModifier)
        LauncherItemType.QUICK_BUTTON -> AureoleDS.icons.Click(iconModifier)
        else -> AureoleDS.icons.Add(iconModifier)
    }
}

@Composable
@ReadOnlyComposable
internal fun getScrollViewChildTitle(type: LauncherItemType?): String {
    return when (type) {
        LauncherItemType.CLOCK -> stringResource(R.string.home_container_add_clock)
        LauncherItemType.APPS_LIST -> stringResource(R.string.home_grid_item_apps_list)
        LauncherItemType.SHORTCUTS_CONTAINER -> stringResource(R.string.home_grid_item_container)
        LauncherItemType.WIDGET_LIST -> stringResource(R.string.home_grid_item_widget_group)
        LauncherItemType.SCROLL_VIEW -> stringResource(R.string.home_container_add_scroll_view)
        LauncherItemType.QUICK_BUTTON -> stringResource(R.string.home_container_add_quick_button)
        else -> stringResource(R.string.home_grid_scroll_component)
    }
}
