package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.search.bestResultRing
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppRowItemParams

/**
 * Search results of the folder picker. The list is reversed and anchored at the bottom: the best result
 * (first) sits next to the search field, with a hint below it. Marked apps show the remove row.
 */
@Composable
internal fun FolderAppPickerSearchList(
    query: String,
    results: List<AppInfo>,
    selectedPackages: List<String>,
    onToggleSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = AureoleDS.dimens.medium),
            contentAlignment = Alignment.BottomCenter
        ) {
            AureoleText(
                text = stringResource(R.string.core_common_search_empty, query),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.Bottom)
    ) {
        item(key = "best_hint") {
            AureoleText(
                text = stringResource(R.string.core_common_search_best_hint),
                color = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.xxSmall),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            )
        }
        itemsIndexed(results, key = { _, app -> "res_${app.packageName}" }) { index, app ->
            val ring = Modifier.bestResultRing(isBest = index == 0)
            if (app.packageName in selectedPackages) {
                SelectedFolderAppRow(
                    params = FolderAppRowItemParams(app = app, index = index, totalCount = 0, canReorder = false),
                    onMoveUp = {},
                    onMoveDown = {},
                    onToggleSelect = { onToggleSelect(app.packageName) },
                    modifier = ring
                )
            } else {
                RemainingFolderAppRow(
                    app = app,
                    onToggleSelect = { onToggleSelect(app.packageName) },
                    modifier = ring
                )
            }
        }
    }
}
