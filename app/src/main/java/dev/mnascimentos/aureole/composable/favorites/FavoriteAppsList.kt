package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.composable.reorder.ReorderableColumn
import dev.mnascimentos.aureole.composable.reorder.model.ReorderAutoScroll
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Sticky header of the favorites (RF-A2): "Favorites · N", the reorder hint when [showReorderHint], and "Remove all"
 * while there is any. Opaque, so the rows scroll under it.
 */
@Composable
internal fun FavoriteSectionHeader(
    count: Int,
    onRemoveAll: () -> Unit,
    showReorderHint: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AureoleDS.colors.surface)
            .padding(top = AureoleDS.dimens.xSmall, bottom = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(text = stringResource(R.string.home_favorites_section_title, count))
        val gapModifier = Modifier
            .weight(1f)
            .padding(horizontal = AureoleDS.dimens.xSmall)
        if (showReorderHint) {
            AureoleText(
                text = stringResource(R.string.home_favorites_section_reorder_hint),
                color = AureoleDS.colors.onSurfaceLow,
                modifier = gapModifier,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
        } else {
            Spacer(modifier = gapModifier)
        }
        if (count > 0) RemoveAllButton(onClick = onRemoveAll)
    }
}

@Composable
private fun RemoveAllButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = stringResource(R.string.home_favorites_disable_all),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        )
    }
}

/** Sticky header of the other apps (RF-A2): "All apps · N", opaque so the rows scroll under it. */
@Composable
internal fun RemainingSectionHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AureoleDS.colors.surface)
            .padding(top = AureoleDS.dimens.small, bottom = AureoleDS.dimens.xxSmall)
    ) {
        SectionTitle(text = stringResource(R.string.home_favorites_section_all_title, count))
    }
}

@Composable
private fun SectionTitle(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceHigh,
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
    stickyHeader(key = "fav_header", contentType = "section_header") {
        FavoriteSectionHeader(
            count = filteredFavorites.size,
            onRemoveAll = actions.onRemoveAll,
            showReorderHint = !isSearching && params.favoritesCount >= MIN_FAVORITES_TO_REORDER
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
            ReorderableFavorites(apps = filteredFavorites, actions = actions, autoScroll = params.autoScroll)
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

/** Reordering needs at least two favorites; the reorder hint of the header shows from there. */
private const val MIN_FAVORITES_TO_REORDER = 2

/** The favorites in their order: hold the handle of one and drag it up or down, ✕ removes it. */
@Composable
private fun ReorderableFavorites(
    apps: List<AppInfo>,
    actions: FavoriteSectionActions,
    autoScroll: ReorderAutoScroll?
) {
    ReorderableColumn(
        params = ReorderableParams(
            items = apps,
            key = { it.packageName },
            actions = ReorderableActions(onMove = actions.onMove, onMoveEnd = actions.onMoveEnd),
            spacing = AureoleDS.dimens.xSmall,
            dragHandleOnly = true
        ),
        autoScroll = autoScroll
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
    stickyHeader(key = "rem_header", contentType = "section_header") {
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
            FavoriteSectionHeader(count = 3, onRemoveAll = {}, showReorderHint = true)
            RemainingSectionHeader(count = 12)
            FavoriteAppsSearchInput(query = "", onQueryChange = {})
        }
    }
}
