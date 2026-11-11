package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
    onRemoveAll: () -> Unit
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f))
                    .clickable(role = Role.Button, onClick = onRemoveAll)
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = stringResource(R.string.home_favorites_disable_all),
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

@Composable
internal fun RemainingSectionHeader(count: Int) {
    AureoleText(
        text = stringResource(R.string.home_folder_other_apps_count, count),
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xxSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
    )
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

    val isSearching = searchQuery.isNotBlank()
    if (!isSearching && params.favoritesCount >= MIN_FAVORITES_TO_REORDER) {
        item(key = "fav_drag_hint", contentType = "fav_notice") { FavoriteAppsDragHint() }
    }
    if (!isSearching && params.favoritesCount == 0) {
        item(key = "fav_empty_banner", contentType = "fav_notice") {
            FavoriteAppsEmptyBanner(appCount = params.allAppsCount)
        }
    }
    item {
        FavoriteSectionHeader(
            count = filteredFavorites.size,
            onRemoveAll = actions.onRemoveAll
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

/** Reordering needs at least two favorites; the drag hint shows from there. */
private const val MIN_FAVORITES_TO_REORDER = 2

/** The favorites in their order: hold the handle of one and drag it up or down, ✕ removes it. */
@Composable
private fun ReorderableFavorites(apps: List<AppInfo>, actions: FavoriteSectionActions) {
    ReorderableColumn(
        params = ReorderableParams(
            items = apps,
            key = { it.packageName },
            actions = ReorderableActions(onMove = actions.onMove, onMoveEnd = actions.onMoveEnd),
            spacing = AureoleDS.dimens.xSmall,
            dragHandleOnly = true
        )
    ) { app ->
        FavoriteAppRow(
            app = app,
            onRemove = { actions.onToggleFavorite(app.packageName) },
            position = apps.indexOf(app) + 1,
            hasDragHandle = true
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.remainingSectionItems(
    filteredRemaining: List<AppInfo>,
    searchQuery: String,
    onToggleFavorite: (String) -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        RemainingSectionHeader(count = filteredRemaining.size)
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

/**
 * One favorite: its [position] number (when given), icon and name, the ✕ when [onRemove] is given and the drag
 * handle when [hasDragHandle] (the handle only works inside a list with `dragHandleOnly`).
 */
@Composable
internal fun FavoriteAppRow(
    app: AppInfo,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
    position: Int? = null,
    hasDragHandle: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (position != null) {
            AureoleText(
                text = position.toString(),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.width(AureoleDS.dimens.large),
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
            )
        }
        FavoriteAppRowInfo(app = app, modifier = Modifier.weight(1f))
        if (onRemove != null) FavoriteAppRemoveButton(onClick = onRemove)
        if (hasDragHandle) FavoriteAppDragHandle()
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
            FavoriteSectionHeader(count = 3, onRemoveAll = {})
            FavoriteAppsSearchInput(query = "", onQueryChange = {})
        }
    }
}
