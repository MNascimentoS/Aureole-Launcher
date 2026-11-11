package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.RemainingFolderAppGridParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.remainingCellKey

/** The drag hint and the four column grid of every app; the ones in the folder carry a check. */
internal fun LazyGridScope.remainingFolderAppSection(params: RemainingFolderAppGridParams) {
    val fullSpan: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    if (params.apps.isEmpty()) {
        item(key = "empty_remaining", span = fullSpan) {
            PickerEmptyText(text = stringResource(R.string.core_common_no_apps_found))
        }
        return
    }
    item(key = "header_remaining", span = fullSpan) {
        AureoleText(
            text = stringResource(R.string.home_folder_picker_drag_hint),
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )
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
    app: AppInfo,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
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
                contentDescription = app.label,
                modifier = Modifier
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = app.label,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        IconButton(
            onClick = onToggleSelect,
            modifier = Modifier.size(AureoleDS.dimens.xLarge)
        ) {
            AureoleDS.icons.Delete(tint = AureoleDS.colors.error)
        }
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
