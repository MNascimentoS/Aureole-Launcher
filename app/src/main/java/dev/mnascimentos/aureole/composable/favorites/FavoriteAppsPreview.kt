package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.model.AppsListSections
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/** What the preview frame of "Choose favorites" draws: the list as the Home shows it. */
data class FavoriteAppsPreviewParams(
    val sections: AppsListSections,
    /** Section labels on, as on the Home: only with "Show all apps" on and "Show labels" on. */
    val hasLabels: Boolean,
    val rowConfig: AppItemRowConfig
)

@Composable
internal fun FavoriteAppsPreviewContent(params: FavoriteAppsPreviewParams) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.small)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
            .border(
                width = AureoleDS.dimens.borderMinimal,
                color = AureoleDS.colors.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
            )
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xxSmall),
        contentAlignment = Alignment.Center
    ) {
        if (params.sections.isEmpty) {
            AureoleText(
                text = stringResource(R.string.home_favorites_none_selected),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.padding(AureoleDS.dimens.medium),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        } else {
            FavoriteAppsPreviewList(params)
        }
    }
}

/** Same order and labels as the Home list: favorites, then the other apps, each under its label when on. */
@Composable
private fun FavoriteAppsPreviewList(params: FavoriteAppsPreviewParams) {
    val sections = params.sections
    val showFavoritesLabel = params.hasLabels && sections.showsAll && sections.favorites.isNotEmpty()
    val showAllLabel = params.hasLabels && sections.showsAll
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (showFavoritesLabel) {
            item(key = "preview_label_favorites", contentType = "preview_label") {
                PreviewSectionLabel(stringResource(R.string.home_apps_list_section_favorites))
            }
        }
        items(sections.favorites, key = { "preview_${it.packageName}" }, contentType = { "preview_app_item" }) {
            AppItemRow(app = it, onClick = {}, config = params.rowConfig)
        }
        if (showAllLabel) {
            item(key = "preview_label_all", contentType = "preview_label") {
                PreviewSectionLabel(stringResource(R.string.home_apps_list_section_all))
            }
        } else if (sections.favorites.isNotEmpty() && sections.others.isNotEmpty()) {
            item(key = "preview_gap", contentType = "preview_gap") {
                Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
            }
        }
        items(sections.others, key = { "preview_other_${it.packageName}" }, contentType = { "preview_app_item" }) {
            AppItemRow(app = it, onClick = {}, config = params.rowConfig)
        }
    }
}

@Composable
private fun PreviewSectionLabel(text: String) {
    AureoleMenuSectionLabel(text = text, modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium))
}
