package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.reorder.ReorderableRow
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.search.AureoleSearchField
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams

/**
 * Footer of the folder picker, glued by the confirm overlay to its action row and to the keyboard. From top to
 * bottom: the reorderable tray of the chosen apps and the search field.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FolderAppPickerFooter(
    params: FolderAppPickerFooterParams,
    actions: FolderAppPickerFooterActions,
    modifier: Modifier = Modifier
) {
    // Empty, the tray only shows its hint while browsing: with the keyboard up the room goes to the results.
    val showTray = params.trayApps.isNotEmpty() || (params.searchQuery.isBlank() && !WindowInsets.isImeVisible)
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        if (showTray) {
            FolderAppPickerTray(
                apps = params.trayApps,
                actions = actions
            )
        }
        AureoleSearchField(
            query = params.searchQuery,
            onQueryChange = actions.onQueryChange,
            placeholder = stringResource(R.string.core_common_search_app_placeholder),
            onSearch = actions.onSearch
        )
    }
}

/**
 * The apps of the folder, in their order: hold one and drag it sideways to reorder, ✕ takes it out. The order reads
 * left to right in both hand modes, since it is the order the folder shows them in.
 */
@Composable
private fun FolderAppPickerTray(
    apps: List<AppInfo>,
    actions: FolderAppPickerFooterActions,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleText(
            text = stringResource(R.string.home_folder_picker_tray_label).uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, fontWeight = FontWeight.Bold)
        )
        if (apps.isEmpty()) {
            AureoleText(
                text = stringResource(R.string.home_folder_no_apps_in_folder),
                color = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier.padding(vertical = AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
            return@Column
        }
        ReorderableRow(
            params = ReorderableParams(
                items = apps,
                key = { it.packageName },
                actions = ReorderableActions(
                    onMove = actions.onMove,
                    onRemove = { index -> apps.getOrNull(index)?.let { actions.onRemove(it.packageName) } }
                ),
                spacing = AureoleDS.dimens.xxSmall
            ),
            modifier = Modifier.fillMaxWidth()
        ) { app -> FolderAppPickerTrayTile(app = app) }
    }
}

@Composable
private fun FolderAppPickerTrayTile(app: AppInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(AureoleDS.dimens.itemHeightExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        AureoleAppIcon(
            app = app,
            size = AureoleDS.dimens.xxLarge,
            params = AureoleAppIconParams(legacyShape = RoundedCornerShape(AureoleDS.dimens.radiusSmall))
        )
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        )
    }
}

@AureolePreview
@Composable
private fun FolderAppPickerFooterPreview() {
    AureoleLauncherTheme {
        FolderAppPickerFooter(
            params = FolderAppPickerFooterParams(trayApps = emptyList(), searchQuery = "cam"),
            actions = FolderAppPickerFooterActions(
                onQueryChange = {},
                onSearch = {},
                onRemove = {},
                onMove = { _, _ -> }
            )
        )
    }
}
