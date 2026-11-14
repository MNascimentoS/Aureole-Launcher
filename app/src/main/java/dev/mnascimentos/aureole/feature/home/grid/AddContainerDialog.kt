package dev.mnascimentos.aureole.feature.home.grid

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeItemIcon
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem

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
        val select = { type: LauncherItemType ->
            onSelectType(type)
            onDismissRequest()
        }
        AddContainerRow(CustomizeAddItem.CLOCK, R.string.home_container_add_clock) { select(LauncherItemType.CLOCK) }
        AddContainerRow(CustomizeAddItem.APPS_LIST, R.string.home_grid_item_apps_list) {
            select(LauncherItemType.APPS_LIST)
        }
        if (!isNested) {
            AddContainerRow(CustomizeAddItem.CONTAINER, R.string.home_container_add_shortcuts_container) {
                select(LauncherItemType.SHORTCUTS_CONTAINER)
            }
        }
        AddContainerRow(CustomizeAddItem.WIDGET_GROUP, R.string.home_container_add_widget_list) {
            select(LauncherItemType.WIDGET_LIST)
        }
        AddContainerRow(CustomizeAddItem.QUICK_BUTTON, R.string.home_container_add_quick_button) {
            select(LauncherItemType.QUICK_BUTTON)
        }
        if (!isNested) {
            AddContainerRow(CustomizeAddItem.SCROLL_GROUP, R.string.home_container_add_scroll_view) {
                select(LauncherItemType.SCROLL_VIEW)
            }
        }
    }
}

/** A row with the same icon as the item has in Customize, on a tile like the reference. */
@Composable
private fun AddContainerRow(item: CustomizeAddItem, @StringRes titleRes: Int, onClick: () -> Unit) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(titleRes),
            onClick = onClick,
            icon = { _, tint ->
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.iconXLarge)
                        .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                        .background(AureoleDS.colors.surfaceVariant.copy(alpha = TILE_ALPHA)),
                    contentAlignment = Alignment.Center
                ) {
                    CustomizeItemIcon(
                        item = item,
                        modifier = Modifier.size(AureoleDS.dimens.iconLargePlus),
                        tint = tint
                    )
                }
            }
        )
    )
}

private const val TILE_ALPHA = 0.6f

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
