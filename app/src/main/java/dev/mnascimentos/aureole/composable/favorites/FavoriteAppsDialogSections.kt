package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
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
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

@Composable
internal fun FavoriteAppsBottomBar(
    currentMode: FavoriteAppsScreenMode,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    val isMain = currentMode == FavoriteAppsScreenMode.MAIN
    val label = if (isMain) R.string.home_favorites_done else R.string.home_favorites_back
    AureolePrimaryActionButton(
        label = stringResource(label),
        onClick = if (isMain) onClose else onBack,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
internal fun FavoriteAppsSheetContent(
    currentMode: FavoriteAppsScreenMode,
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    onModeChange: (FavoriteAppsScreenMode) -> Unit
) {
    when (currentMode) {
        FavoriteAppsScreenMode.MAIN -> FavoriteAppsMainContent(
            config = config,
            actions = actions,
            onModeChange = onModeChange
        )
        FavoriteAppsScreenMode.MANAGE,
        FavoriteAppsScreenMode.EDIT_SHORTCUTS -> FavoriteAppsDialogContent(config = config, actions = actions)
        FavoriteAppsScreenMode.APPEARANCE -> FavoriteAppsAppearanceContent(config = config, actions = actions)
    }
}

private const val SEARCH_DEBOUNCE_DELAY_MS = 300L

@Composable
private fun FavoriteAppsDialogContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    var searchQuery by remember { mutableStateOf("") }
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
        ),
        onSearchQueryChange = { searchQuery = it }
    )
}

private fun filterAppsByQuery(apps: List<AppInfo>, query: String): List<AppInfo> {
    if (query.isBlank()) return apps
    return apps.filter { it.label.contains(query, ignoreCase = true) }
}

@Composable
private fun FavoriteAppsDialogBody(
    params: FavoriteAppsBodyParams,
    onSearchQueryChange: (String) -> Unit
) {
    val config = params.config
    val actions = params.actions
    // Draft order while a favorite is dragged; saved once, when the gesture ends.
    var draftFavorites by remember(params.filteredFavorites) { mutableStateOf(params.filteredFavorites) }
    val handleMoveEnd = {
        val order = reorderFavoritePackages(config.favoriteAppPackages, draftFavorites.map { it.packageName })
        if (order != config.favoriteAppPackages) actions.onUpdateFavoritePackages(config.containerId, order)
    }

    val handleActivateAll = {
        val toAdd = params.filteredRemaining.map { it.packageName }
        val newFavs = (config.favoriteAppPackages + toAdd).distinct()
        actions.onUpdateFavoritePackages(config.containerId, newFavs)
    }

    val handleDeactivateAll = {
        val toRemove = params.filteredFavorites.map { it.packageName }.toSet()
        val newFavs = config.favoriteAppPackages.filter { it !in toRemove }
        actions.onUpdateFavoritePackages(config.containerId, newFavs)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        FavoriteAppsSearchInput(
            query = params.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        if (config.isLoading) {
            AppListSkeletonComponent(modifier = Modifier.weight(1f))
            return@Column
        }

        LeftHandMirror {
            FavoriteAppsSections(
                params = params.copy(filteredFavorites = draftFavorites),
                actions = FavoriteSectionActions(
                    onMove = { from, to -> draftFavorites = move(draftFavorites, from, to) },
                    onMoveEnd = handleMoveEnd,
                    onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) },
                    onDeactivateAll = handleDeactivateAll
                ),
                onActivateAll = handleActivateAll,
                modifier = Modifier.weight(1f)
            )
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
    onActivateAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        favoriteSectionItems(
            params = FavoriteSectionParams(
                filteredFavorites = params.filteredFavorites,
                searchQuery = params.searchQuery
            ),
            actions = actions
        )
        remainingSectionItems(
            filteredRemaining = params.filteredRemaining,
            searchQuery = params.searchQuery,
            onToggleFavorite = actions.onToggleFavorite,
            onActivateAll = onActivateAll
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
