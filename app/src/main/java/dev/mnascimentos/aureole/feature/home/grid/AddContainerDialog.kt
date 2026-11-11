package dev.mnascimentos.aureole.feature.home.grid

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Bottom menu to pick which kind of container to add; every row is within reach of the thumb. */
@Composable
fun AddContainerDialog(
    onDismissRequest: () -> Unit,
    onSelectType: (LauncherItemType) -> Unit,
    modifier: Modifier = Modifier,
    isNested: Boolean = false
) {
    val titleText = if (isNested) {
        stringResource(R.string.home_container_add_nested_title)
    } else {
        stringResource(R.string.home_container_add_title)
    }
    AureoleMenuSheet(title = titleText, onDismiss = onDismissRequest, modifier = modifier) {
        AddContainerRow(R.string.home_container_add_clock, { m, c -> AureoleDS.icons.Calendar(m, c) }) {
            onSelectType(LauncherItemType.CLOCK)
            onDismissRequest()
        }
        AddContainerRow(R.string.home_container_add_apps_list, { m, c -> AureoleDS.icons.Star(m, c) }) {
            onSelectType(LauncherItemType.APPS_LIST)
            onDismissRequest()
        }
        AddContainerRow(
            R.string.home_container_add_all_apps_list,
            { m, c -> AureoleDS.icons.HorizontalContainer(m, c) }
        ) {
            onSelectType(LauncherItemType.ALL_APPS_LIST)
            onDismissRequest()
        }
        if (!isNested) {
            AddContainerRow(
                R.string.home_container_add_shortcuts_container,
                { m, c -> AureoleDS.icons.MenuIcon(m, c) }
            ) {
                onSelectType(LauncherItemType.SHORTCUTS_CONTAINER)
                onDismissRequest()
            }
        }
        AddContainerRow(R.string.home_container_add_widget_list, { m, c -> AureoleDS.icons.Add(m, c) }) {
            onSelectType(LauncherItemType.WIDGET_LIST)
            onDismissRequest()
        }
        AddContainerRow(R.string.home_container_add_quick_button, { m, c -> AureoleDS.icons.Click(m, c) }) {
            onSelectType(LauncherItemType.QUICK_BUTTON)
            onDismissRequest()
        }
        if (!isNested) {
            AddContainerRow(
                R.string.home_container_add_scroll_view,
                { m, c -> AureoleDS.icons.VerticalContainer(m, c) }
            ) {
                onSelectType(LauncherItemType.SCROLL_VIEW)
                onDismissRequest()
            }
        }
    }
}

@Composable
private fun AddContainerRow(
    @StringRes titleRes: Int,
    icon: @Composable (Modifier, Color) -> Unit,
    onClick: () -> Unit
) {
    AureoleMenuRow(MenuRowParams(title = stringResource(titleRes), onClick = onClick, icon = icon))
}

@AureolePreview
@Composable
private fun AddContainerDialogPreview() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {}
        )
    }
}
