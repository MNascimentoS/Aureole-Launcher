package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.composable.reorder.ReorderableColumn
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
internal fun FavoriteSectionHeader(
    count: Int,
    onDeactivateAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = stringResource(R.string.home_favorites_count, count),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
        )
        if (count > 0) {
            TextButton(
                onClick = onDeactivateAll,
                contentPadding = PaddingValues(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.zero)
            ) {
                AureoleText(
                    text = stringResource(R.string.home_favorites_disable_all),
                    color = AureoleDS.colors.error,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
internal fun RemainingSectionHeader(
    count: Int,
    onActivateAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = stringResource(R.string.home_folder_other_apps_count, count),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
        )
        if (count > 0) {
            TextButton(
                onClick = onActivateAll,
                contentPadding = PaddingValues(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.zero)
            ) {
                AureoleText(
                    text = stringResource(R.string.home_favorites_enable_all),
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Header and rows of the favorites. With the search empty they form one reorderable list (hold and drag, ✕
 * removes); while searching they are plain rows, since a filtered list has no order to change.
 */
@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.favoriteSectionItems(
    params: FavoriteSectionParams,
    actions: FavoriteSectionActions
) {
    val filteredFavorites = params.filteredFavorites
    val searchQuery = params.searchQuery

    item {
        FavoriteSectionHeader(
            count = filteredFavorites.size,
            onDeactivateAll = actions.onDeactivateAll
        )
    }

    when {
        filteredFavorites.isEmpty() -> item {
            val emptyMsg = if (searchQuery.isBlank()) {
                stringResource(R.string.home_favorites_none_selected)
            } else {
                stringResource(R.string.home_favorites_none_found)
            }
            AureoleText(
                text = emptyMsg,
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
            )
        }
        searchQuery.isBlank() -> item(key = "fav_reorderable", contentType = "fav_reorderable") {
            ReorderableFavorites(apps = filteredFavorites, actions = actions)
        }
        else -> items(
            items = filteredFavorites,
            key = { app -> "fav_${app.packageName}" },
            contentType = { "fav_app_row" }
        ) { app ->
            FavoriteAppRow(
                app = app,
                onRemove = { actions.onToggleFavorite(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

/** The favorites in their order: hold one and drag it up or down, ✕ removes it. */
@Composable
private fun ReorderableFavorites(apps: List<AppInfo>, actions: FavoriteSectionActions) {
    ReorderableColumn(
        params = ReorderableParams(
            items = apps,
            key = { it.packageName },
            actions = ReorderableActions(
                onMove = actions.onMove,
                onMoveEnd = actions.onMoveEnd,
                onRemove = { index -> apps.getOrNull(index)?.let { actions.onToggleFavorite(it.packageName) } }
            ),
            spacing = AureoleDS.dimens.xSmall
        )
    ) { app -> FavoriteAppRow(app = app) }
}

@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.remainingSectionItems(
    filteredRemaining: List<AppInfo>,
    searchQuery: String,
    onToggleFavorite: (String) -> Unit,
    onActivateAll: () -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        RemainingSectionHeader(
            count = filteredRemaining.size,
            onActivateAll = onActivateAll
        )
    }

    if (filteredRemaining.isEmpty()) {
        item {
            val emptyMsg = if (searchQuery.isBlank()) {
                stringResource(R.string.home_favorites_all_added)
            } else {
                stringResource(R.string.home_favorites_none_other_found)
            }
            AureoleText(
                text = emptyMsg,
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
            )
        }
    } else {
        itemsIndexed(
            items = filteredRemaining,
            key = { _, app -> "rem_${app.packageName}" },
            contentType = { _, _ -> "rem_app_row" }
        ) { _, app ->
            RemainingAppRow(
                app = app,
                onToggleFavorite = { onToggleFavorite(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

/** One favorite: icon and name, plus the remove button when [onRemove] is given (the reorderable list uses ✕). */
@Composable
internal fun FavoriteAppRow(
    app: AppInfo,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.smallCompact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FavoriteAppRowInfo(app = app, modifier = Modifier.weight(1f))
        if (onRemove != null) FavoriteAppRemoveButton(onClick = onRemove)
    }
}

@Composable
internal fun FavoriteAppsSearchInput(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { AureoleText(stringResource(R.string.home_favorites_search_placeholder)) },
        leadingIcon = {
            AureoleDS.icons.Search(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedBorderColor = AureoleDS.colors.outline.copy(alpha = 0.5f),
            focusedTextColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
        ),
        shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall),
        modifier = Modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
private fun FavoriteAppsListPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            FavoriteSectionHeader(count = 3, onDeactivateAll = {})
            FavoriteAppsSearchInput(query = "", onQueryChange = {})
        }
    }
}
