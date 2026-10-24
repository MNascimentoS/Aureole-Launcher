@file:Suppress("TooManyFunctions", "MagicNumber")

package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteSectionParams
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
        AureoleText(if (isMain) stringResource(R.string.favorites_done) else stringResource(R.string.favorites_back))
    }
}

@Composable
internal fun FavoriteAppsSheetContent(
    currentMode: FavoriteAppsScreenMode,
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    onEditShortcutsClick: () -> Unit,
    onAppearanceClick: () -> Unit
) {
    when (currentMode) {
        FavoriteAppsScreenMode.MAIN -> {
            MainFavoritesContent(
                onEditShortcutsClick = onEditShortcutsClick,
                onAppearanceClick = onAppearanceClick
            )
        }
        FavoriteAppsScreenMode.APPEARANCE -> {
            AppearanceFavoritesContent(
                config = config,
                actions = actions
            )
        }
        FavoriteAppsScreenMode.EDIT_SHORTCUTS -> {
            FavoriteAppsDialogContent(config = config, actions = actions)
        }
    }
}

@Composable
private fun MainFavoritesContent(
    onEditShortcutsClick: () -> Unit,
    onAppearanceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainFavoritesEditShortcutsRow(onClick = onEditShortcutsClick)
        MainFavoritesAppearanceRow(onClick = onAppearanceClick)
    }
}

@Composable
private fun MainFavoritesEditShortcutsRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Star(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.favorites_edit_shortcuts),
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh
            )
            Text(
                text = stringResource(R.string.favorites_manage_apps_order),
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
private fun MainFavoritesAppearanceRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Edit(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.edit_container_appearance),
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh
            )
            Text(
                text = stringResource(R.string.favorites_appearance_desc),
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
private fun AppearanceFavoritesContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    FavoriteAppsShowAllSwitchRow(
        showAllAppsOnHome = config.showAllAppsOnHome,
        onToggleShowAllAppsOnHome = actions.onToggleShowAllAppsOnHome
    )
}

@Composable
private fun FavoriteAppsDialogContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
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
            currentMode = FavoriteAppsScreenMode.MAIN,
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions(),
            onEditShortcutsClick = {},
            onAppearanceClick = {}
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentAppearancePreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            currentMode = FavoriteAppsScreenMode.APPEARANCE,
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList(),
                showAllAppsOnHome = true
            ),
            actions = FavoriteAppsDialogActions(),
            onEditShortcutsClick = {},
            onAppearanceClick = {}
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsSheetContentEditShortcutsPreview() {
    AureoleLauncherTheme {
        FavoriteAppsSheetContent(
            currentMode = FavoriteAppsScreenMode.EDIT_SHORTCUTS,
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions(),
            onEditShortcutsClick = {},
            onAppearanceClick = {}
        )
    }
}
