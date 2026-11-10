package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
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
        LauncherItemType.APPS_LIST -> stringResource(R.string.home_grid_item_favorites)
        LauncherItemType.ALL_APPS_LIST -> stringResource(R.string.home_container_add_all_apps_list)
        LauncherItemType.SHORTCUTS_CONTAINER -> stringResource(R.string.home_grid_item_shortcuts)
        LauncherItemType.SINGLE_APP_WIDGET -> stringResource(R.string.home_grid_item_widget)
        LauncherItemType.WIDGET_LIST -> stringResource(R.string.home_grid_item_widget_group)
        LauncherItemType.SCROLL_VIEW -> stringResource(R.string.home_grid_item_scroll_view)
        LauncherItemType.QUICK_BUTTON -> stringResource(R.string.home_grid_item_quick_button)
        else -> stringResource(R.string.home_grid_item_container)
    }

    SettingsBottomSheet(
        title = stringResource(R.string.home_grid_config_title, title),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = stringResource(R.string.home_grid_current_size, item.colSpan, item.rowSpan),
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            EditDialogTypeActionSection(
                item = item,
                actions = actions,
                onDismissRequest = onDismissRequest
            )

            if (item.type == LauncherItemType.SCROLL_VIEW) {
                EditDialogScrollViewSection(item = item, actions = actions, onDismissRequest = onDismissRequest)
            }

            EditDialogDeleteRow(
                itemId = item.id,
                onDeleteConfirm = onDeleteConfirm,
                onDismissRequest = onDismissRequest
            )
        }
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
