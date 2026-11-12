package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureolePrimaryActionButton
import dev.mnascimentos.aureole.composable.appitem.AppListSkeletonComponent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.composable.favorites.model.reorderFavoritePackages
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.composable.reorder.rememberReorderAutoScroll
import dev.mnascimentos.aureole.composable.reorder.reorderAutoScrollViewport
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

@Composable
internal fun FavoriteAppsBottomBar(
    onBack: () -> Unit,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FavoriteAppsSearchInput(query = searchQuery, onQueryChange = onSearchQueryChange)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        AureolePrimaryActionButton(
            label = stringResource(R.string.home_favorites_back),
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun FavoriteAppsSheetContent(
    currentMode: FavoriteAppsScreenMode,
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    onModeChange: (FavoriteAppsScreenMode) -> Unit,
    searchQuery: String = ""
) {
    when (currentMode) {
        FavoriteAppsScreenMode.MAIN -> FavoriteAppsMainContent(config = config, onModeChange = onModeChange)
        FavoriteAppsScreenMode.MANAGE,
        FavoriteAppsScreenMode.EDIT_SHORTCUTS -> FavoriteAppsDialogContent(
            config = config,
            actions = actions,
            searchQuery = searchQuery
        )
    }
}

private const val SEARCH_DEBOUNCE_DELAY_MS = 300L

@Composable
private fun FavoriteAppsDialogContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    searchQuery: String
) {
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(SEARCH_DEBOUNCE_DELAY_MS)
        debouncedQuery = searchQuery
    }

    val appMap = remember(config.allApps) {
        config.allApps.associateBy { it.packageName }
    }

    val favoriteApps = remember(config.favoriteAppPackages, appMap) {
        config.favoriteAppPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    val remainingApps = remember(config.allApps, config.favoriteAppPackages) {
        val favSet = config.favoriteAppPackages.toSet()
        config.allApps.filter { it.packageName !in favSet }
            .sortedBy { it.label.lowercase() }
    }

    val filteredFavorites = remember(debouncedQuery, favoriteApps) {
        filterAppsByQuery(favoriteApps, debouncedQuery)
    }

    val filteredRemaining = remember(debouncedQuery, remainingApps) {
        filterAppsByQuery(remainingApps, debouncedQuery)
    }

    FavoriteAppsDialogBody(
        params = FavoriteAppsBodyParams(
            config = config,
            actions = actions,
            searchQuery = searchQuery,
            filteredFavorites = filteredFavorites,
            filteredRemaining = filteredRemaining
        )
    )
}

private fun filterAppsByQuery(apps: List<AppInfo>, query: String): List<AppInfo> {
    if (query.isBlank()) return apps
    return apps.filter { it.label.contains(query, ignoreCase = true) }
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
 * Both sections, favorites then the other apps. [FavoriteAppsDialogBody] mirrors them together in the left-handed
 * mode, so the ✕ of a favorite and the switch of an other app sit on the same side, under the thumb.
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
        favoriteSectionItems(
            params = FavoriteSectionParams(
                filteredFavorites = params.filteredFavorites,
                searchQuery = params.searchQuery,
                favoritesCount = params.config.favoriteAppPackages.size,
                allAppsCount = params.config.allApps.size,
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
            currentMode = FavoriteAppsScreenMode.MANAGE,
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions(),
            onModeChange = {}
        )
    }
}
