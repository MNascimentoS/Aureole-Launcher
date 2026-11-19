package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.appitem.AppListSkeletonComponent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.favoriteAppsLists
import dev.mnascimentos.aureole.composable.favorites.model.favoritesEmptyNotice
import dev.mnascimentos.aureole.composable.favorites.model.filteredBy
import dev.mnascimentos.aureole.composable.favorites.model.reorderFavoritePackages
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.composable.reorder.rememberReorderAutoScroll
import dev.mnascimentos.aureole.composable.reorder.reorderAutoScrollViewport
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

/**
 * The footer of the sheet: the search field and, when [options] is given (landscape, RF-05), the options chips on
 * the same line, so the top of the sheet is only the title and the list shows without scrolling. The sheet has no
 * back button; the system back closes it.
 */
@Composable
internal fun FavoriteAppsBottomBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    options: FavoriteAppsDialogConfig? = null
) {
    if (options?.showAllApps == null) {
        FavoriteAppsSearchInput(query = searchQuery, onQueryChange = onSearchQueryChange)
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        FavoriteAppsListHeader(config = options)
        Box(modifier = Modifier.weight(1f)) {
            FavoriteAppsSearchInput(query = searchQuery, onQueryChange = onSearchQueryChange)
        }
    }
}

/** The single "Choose favorites" screen: one list with the options chips over the apps to pick and order. */
@Composable
internal fun FavoriteAppsSheetContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    searchQuery: String = "",
    showOptions: Boolean = true
) {
    FavoriteAppsDialogContent(config = config, actions = actions, searchQuery = searchQuery, showOptions = showOptions)
}

private const val SEARCH_DEBOUNCE_DELAY_MS = 300L

@Composable
private fun FavoriteAppsDialogContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    searchQuery: String,
    showOptions: Boolean
) {
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(SEARCH_DEBOUNCE_DELAY_MS)
        debouncedQuery = searchQuery
    }

    val lists = remember(config.allApps, config.favoriteAppPackages) {
        favoriteAppsLists(config.allApps, config.favoriteAppPackages)
    }
    val filtered = remember(debouncedQuery, lists) { lists.filteredBy(debouncedQuery) }

    FavoriteAppsDialogBody(
        params = FavoriteAppsBodyParams(
            config = config,
            actions = actions,
            searchQuery = searchQuery,
            filteredFavorites = filtered.favorites,
            filteredRemaining = filtered.remaining,
            showOptions = showOptions
        )
    )
}

@Composable
private fun FavoriteAppsDialogBody(
    params: FavoriteAppsBodyParams
) {
    val config = params.config
    val actions = params.actions
    // Draft order while a favorite is dragged; saved once, when the gesture ends.
    var draftFavorites by remember(params.filteredFavorites) { mutableStateOf(params.filteredFavorites) }
    val handleMoveEnd = {
        val order = reorderFavoritePackages(config.favoriteAppPackages, draftFavorites.map { it.packageName })
        if (order != config.favoriteAppPackages) actions.onUpdateFavoritePackages(config.containerId, order)
    }

    val handleRemoveAll = {
        val toRemove = params.filteredFavorites.map { it.packageName }.toSet()
        val newFavs = config.favoriteAppPackages.filter { it !in toRemove }
        actions.onUpdateFavoritePackages(config.containerId, newFavs)
    }

    // The search lives in the footer of the sheet (see FavoriteAppsBottomBar), below the divider.
    Column(modifier = Modifier.fillMaxWidth()) {
        if (config.isLoading) {
            if (params.showOptions) FavoriteAppsListHeader(config = config)
            AppListSkeletonComponent(modifier = Modifier.weight(1f))
        } else {
            LeftHandMirror {
                FavoriteAppsSections(
                    params = params.copy(filteredFavorites = draftFavorites),
                    actions = FavoriteSectionActions(
                        onMove = { from, to -> draftFavorites = move(draftFavorites, from, to) },
                        onMoveEnd = handleMoveEnd,
                        onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) },
                        onRemoveAll = handleRemoveAll
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * The one scroll of the sheet (RF-01): the options chips and the notice without favorites, then both sections,
 * favorites and the other apps, each under a sticky header (RF-A2). [FavoriteAppsDialogBody] mirrors them together
 * in the left-handed mode, so the − of a favorite and the + of an other app sit on the same side, under the thumb.
 */
@Composable
private fun FavoriteAppsSections(
    params: FavoriteAppsBodyParams,
    actions: FavoriteSectionActions,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val autoScroll = rememberReorderAutoScroll(listState)
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .reorderAutoScrollViewport(autoScroll),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        val config = params.config
        if (config.showAllApps != null && params.showOptions) {
            item(key = "options", contentType = "options") { FavoriteAppsListHeader(config = config) }
        }
        favoritesEmptyNotice(
            favoriteCount = config.favoriteAppPackages.size,
            showAllApps = config.showAllApps?.checked,
            isSearching = params.searchQuery.isNotBlank()
        )?.let { notice ->
            item(key = "empty_notice", contentType = "empty_notice") {
                FavoriteAppsEmptyNotice(notice = notice, appCount = config.allApps.size)
            }
        }
        favoriteSectionItems(
            params = FavoriteSectionParams(
                filteredFavorites = params.filteredFavorites,
                searchQuery = params.searchQuery,
                favoritesCount = config.favoriteAppPackages.size,
                autoScroll = autoScroll
            ),
            actions = actions
        )
        remainingSectionItems(
            filteredRemaining = params.filteredRemaining,
            searchQuery = params.searchQuery,
            onToggleFavorite = actions.onToggleFavorite
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentManagePreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList(),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {})
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}
