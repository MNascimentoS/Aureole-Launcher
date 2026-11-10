package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AppListSkeletonComponent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
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
    Button(
        onClick = if (isMain) onClose else onBack,
        modifier = Modifier.fillMaxWidth()
    ) {
        AureoleText(
            if (isMain) stringResource(R.string.home_favorites_done) else stringResource(R.string.home_favorites_back)
        )
    }
}

@Composable
internal fun FavoriteAppsSheetContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    FavoriteAppsDialogContent(config = config, actions = actions)
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
            favoriteCount = favoriteApps.size,
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
    val handleMoveUp = { index: Int -> moveFavoriteItem(config, actions, index, delta = -1) }
    val handleMoveDown = { index: Int -> moveFavoriteItem(config, actions, index, delta = 1) }

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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            favoriteSectionItems(
                params = FavoriteSectionParams(
                    filteredFavorites = params.filteredFavorites,
                    searchQuery = params.searchQuery,
                    favoriteCount = params.favoriteCount
                ),
                onMoveUp = handleMoveUp,
                onMoveDown = handleMoveDown,
                onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) },
                onDeactivateAll = handleDeactivateAll
            )

            remainingSectionItems(
                filteredRemaining = params.filteredRemaining,
                searchQuery = params.searchQuery,
                onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) },
                onActivateAll = handleActivateAll
            )
        }
    }
}

private fun moveFavoriteItem(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    index: Int,
    delta: Int
) {
    val targetIndex = index + delta
    if (targetIndex !in config.favoriteAppPackages.indices) return

    val list = config.favoriteAppPackages.toMutableList()
    val item = list.removeAt(index)
    list.add(targetIndex, item)
    actions.onUpdateFavoritePackages(config.containerId, list)
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentMainPreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentAppearancePreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentEditShortcutsPreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}
