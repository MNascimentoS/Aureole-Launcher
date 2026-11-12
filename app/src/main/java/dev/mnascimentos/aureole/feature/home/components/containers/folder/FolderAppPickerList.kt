package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppRowItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.RemainingFolderAppGridParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.SelectedFolderAppSectionParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.remainingCellKey
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.selectedRowKey

private const val UNMARKED_ROW_ALPHA = 0.4f

/** Header of the selected apps and one full width row per app, with the reorder arrows. */
internal fun LazyGridScope.selectedFolderAppSection(
    params: SelectedFolderAppSectionParams
) {
    val totalSelectedCount = params.totalSelectedCount
    val fullSpan: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    item(key = "header_selected", span = fullSpan) {
        PickerSectionTitle(
            text = stringResource(R.string.home_folder_selected_apps_count, totalSelectedCount),
            color = AureoleDS.colors.onSurfaceHigh
        )
    }

    if (params.filteredSelected.isEmpty()) {
        item(key = "empty_selected", span = fullSpan) {
            PickerEmptyText(
                text = if (params.searchQuery.isBlank()) {
                    stringResource(R.string.home_folder_no_apps_in_folder)
                } else {
                    stringResource(R.string.core_common_no_apps_found)
                }
            )
        }
        return
    }
    itemsIndexed(
        items = params.filteredSelected,
        key = { _, app -> selectedRowKey(app.packageName) },
        span = { _, _ -> GridItemSpan(maxLineSpan) },
        contentType = { _, _ -> "selected_app_row" }
    ) { index, app ->
        val alpha = if (params.isMarked(app.packageName)) 1f else UNMARKED_ROW_ALPHA
        SelectedFolderAppRow(
            params = FolderAppRowItemParams(
                app = app,
                index = index,
                totalCount = totalSelectedCount,
                canReorder = params.searchQuery.isBlank() && params.isReorderEnabled
            ),
            onMoveUp = { params.onMoveUp(index) },
            onMoveDown = { params.onMoveDown(index) },
            onToggleSelect = { params.onToggleSelect(app.packageName) },
            modifier = Modifier
                .animateItem()
                .alpha(alpha)
        )
    }
}

/** Header of the other apps, with the drag hint, and the four column grid of their cells. */
internal fun LazyGridScope.remainingFolderAppSection(params: RemainingFolderAppGridParams) {
    val fullSpan: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    item(key = "header_remaining", span = fullSpan) {
        Column {
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
            PickerSectionTitle(
                text = stringResource(R.string.home_folder_other_apps_count, params.apps.size),
                color = AureoleDS.colors.onSurfaceMedium
            )
            if (params.apps.isNotEmpty()) {
                AureoleText(
                    text = stringResource(R.string.home_folder_picker_drag_hint),
                    color = AureoleDS.colors.onSurfaceLow,
                    modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
                    format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
                )
            }
        }
    }

    if (params.apps.isEmpty()) {
        item(key = "empty_remaining", span = fullSpan) {
            PickerEmptyText(
                text = if (params.searchQuery.isBlank()) {
                    stringResource(R.string.home_folder_all_apps_added)
                } else {
                    stringResource(R.string.core_common_no_apps_found)
                }
            )
        }
        return
    }
    items(
        items = params.apps,
        key = { app -> remainingCellKey(app.packageName) },
        contentType = { "remaining_app_cell" }
    ) { app ->
        FolderAppGridCell(
            app = app,
            isMarked = params.isMarked(app.packageName),
            isSelecting = params.isSelecting,
            onToggle = { params.onToggleSelect(app.packageName) }
        )
    }
}

@Composable
private fun PickerSectionTitle(text: String, color: Color) {
    AureoleText(
        text = text,
        color = color,
        modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
    )
}

@Composable
private fun PickerEmptyText(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.small),
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
    )
}

@Composable
internal fun SelectedFolderAppRow(
    params: FolderAppRowItemParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(params.app.packageName) { params.app.getIconBitmap() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = params.app.label,
                modifier = Modifier
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = params.app.label,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        SelectedFolderAppRowActionButtons(
            params = params,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onToggleSelect = onToggleSelect
        )
    }
}

@AureolePreview
@Composable
private fun FolderAppPickerDialogPreview() {
    val mockFolder = AppFolder(id = "1", name = "Social")
    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = mockFolder,
            allApps = emptyList(),
            onDismiss = {},
            onSave = {}
        )
    }
}
