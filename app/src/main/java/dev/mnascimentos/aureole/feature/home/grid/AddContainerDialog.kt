package dev.mnascimentos.aureole.feature.home.grid

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeItemIcon
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.grid.model.AddComponentMetrics

/**
 * "Add component" (grupo-apps-pastas RF-A1, RF-A2): what can go on the home, each row with a line saying what it is.
 * While the first group's tips were not seen, the app & folder group row is highlighted with "Start here" (RF-A3,
 * [showsGroupStartHere]).
 */
@Composable
fun AddContainerDialog(
    onDismissRequest: () -> Unit,
    onSelectType: (LauncherItemType) -> Unit,
    modifier: Modifier = Modifier,
    isNested: Boolean = false,
    showsGroupStartHere: Boolean = false
) {
    val titleText = if (isNested) {
        stringResource(R.string.home_container_add_nested_title)
    } else {
        stringResource(R.string.home_container_add_title)
    }
    AureoleMenuSheet(
        title = titleText,
        onDismiss = onDismissRequest,
        modifier = modifier,
        params = MenuSheetParams(subtitle = stringResource(R.string.home_container_add_subtitle))
    ) {
        val select = { type: LauncherItemType ->
            onSelectType(type)
            onDismissRequest()
        }
        AddContainerRow(CustomizeAddItem.CLOCK, R.string.home_container_add_clock, R.string.home_container_add_clock_subtitle) {
            select(LauncherItemType.CLOCK)
        }
        AddContainerRow(
            CustomizeAddItem.APPS_LIST,
            R.string.home_grid_item_apps_list,
            R.string.home_container_add_apps_list_subtitle
        ) { select(LauncherItemType.APPS_LIST) }
        if (!isNested) {
            AddGroupRow(showsStartHere = showsGroupStartHere) { select(LauncherItemType.SHORTCUTS_CONTAINER) }
        }
        AddContainerRow(
            CustomizeAddItem.WIDGET_GROUP,
            R.string.home_container_add_widget_list,
            R.string.home_container_add_widget_list_subtitle
        ) { select(LauncherItemType.WIDGET_LIST) }
        AddContainerRow(
            CustomizeAddItem.QUICK_BUTTON,
            R.string.home_container_add_quick_button,
            R.string.home_container_add_quick_button_subtitle
        ) { select(LauncherItemType.QUICK_BUTTON) }
        if (!isNested) {
            AddContainerRow(
                CustomizeAddItem.SCROLL_GROUP,
                R.string.home_container_add_scroll_view,
                R.string.home_container_add_scroll_view_subtitle
            ) { select(LauncherItemType.SCROLL_VIEW) }
        }
    }
}

/** The group row; with [showsStartHere] it sits on a lighter tile with the "Start here" pill at its end (RF-A3). */
@Composable
private fun AddGroupRow(showsStartHere: Boolean, onClick: () -> Unit) {
    if (!showsStartHere) {
        AddContainerRow(
            CustomizeAddItem.CONTAINER,
            R.string.home_container_add_shortcuts_container,
            R.string.home_container_add_group_subtitle,
            onClick = onClick
        )
        return
    }
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = AddComponentMetrics.HIGHLIGHT_ALPHA))
    ) {
        AddContainerRow(
            CustomizeAddItem.CONTAINER,
            R.string.home_container_add_shortcuts_container,
            R.string.home_container_add_group_subtitle,
            onClick = onClick
        )
        StartHerePill(modifier = Modifier.align(Alignment.CenterEnd).padding(end = AureoleDS.dimens.small))
    }
}

@Composable
private fun StartHerePill(modifier: Modifier = Modifier) {
    AureoleText(
        text = stringResource(R.string.home_container_add_start_here),
        color = AureoleDS.colors.surface,
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.onSurfaceHigh)
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.minimal),
        format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, fontWeight = FontWeight.Bold)
    )
}

/** A row with the same icon as the item has in Customize, on a tile like the reference, and what it is below. */
@Composable
private fun AddContainerRow(
    item: CustomizeAddItem,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    onClick: () -> Unit
) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(titleRes),
            subtitle = stringResource(subtitleRes),
            onClick = onClick,
            icon = { _, tint ->
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.iconXLarge)
                        .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                        .background(AureoleDS.colors.surfaceVariant.copy(alpha = AddComponentMetrics.TILE_ALPHA)),
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

@AureolePreview
@Composable
private fun AddContainerDialogPreview() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {},
            showsGroupStartHere = true
        )
    }
}
