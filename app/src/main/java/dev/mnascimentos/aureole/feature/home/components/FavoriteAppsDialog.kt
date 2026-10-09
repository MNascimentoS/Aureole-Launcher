@file:Suppress("TooManyFunctions")

package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppRowParams
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteSectionParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAppsDialog(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    val sheetContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AureoleDS.dimens.large)
                .padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.large)
        ) {
            FavoriteAppsDialogContent(config = config, actions = actions)
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius
                ),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = actions.onDismiss,
            sheetState = sheetState,
            dragHandle = null,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        ) {
            sheetContent()
        }
    }
}


@Composable
private fun FavoriteAppsDialogContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    var searchQuery by remember { mutableStateOf("") }

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

    val filteredFavorites = remember(searchQuery, favoriteApps) {
        filterAppsByQuery(favoriteApps, searchQuery)
    }

    val filteredRemaining = remember(searchQuery, remainingApps) {
        filterAppsByQuery(remainingApps, searchQuery)
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
    return if (query.isBlank()) {
        apps
    } else {
        apps.filter { it.label.contains(query, ignoreCase = true) }
    }
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
        FavoriteAppsDialogHeader(onDismiss = actions.onDismiss, containerId = config.containerId)
        Spacer(modifier = Modifier.height(12.dp))

        FavoriteAppsShowAllSwitchRow(
            showAllAppsOnHome = config.showAllAppsOnHome,
            onToggleShowAllAppsOnHome = actions.onToggleShowAllAppsOnHome
        )
        Spacer(modifier = Modifier.height(12.dp))

        FavoriteAppsSearchInput(
            query = params.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = actions.onDismiss, modifier = Modifier.align(Alignment.End)) {
            AureoleText("Concluído")
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
    if (targetIndex in 0 until config.favoriteAppPackages.size) {
        val list = config.favoriteAppPackages.toMutableList()
        val item = list.removeAt(index)
        list.add(targetIndex, item)
        actions.onUpdateFavoritePackages(config.containerId, list)
    }
}

@Composable
private fun FavoriteSectionHeader(
    count: Int,
    onDeactivateAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = "Favoritos ($count)",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (count > 0) {
            TextButton(
                onClick = onDeactivateAll,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                AureoleText(
                    text = "Desativar todos",
                    style = AureoleTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun RemainingSectionHeader(
    count: Int,
    onActivateAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = "Outros Aplicativos ($count)",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceMedium
        )
        if (count > 0) {
            TextButton(
                onClick = onActivateAll,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                AureoleText(
                    text = "Ativar todos",
                    style = AureoleTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.favoriteSectionItems(
    params: FavoriteSectionParams,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDeactivateAll: () -> Unit
) {
    val filteredFavorites = params.filteredFavorites
    val searchQuery = params.searchQuery
    val favoriteCount = params.favoriteCount

    item {
        FavoriteSectionHeader(
            count = filteredFavorites.size,
            onDeactivateAll = onDeactivateAll
        )
    }

    if (filteredFavorites.isEmpty()) {
        item {
            val emptyMsg = if (searchQuery.isBlank()) {
                "Nenhum aplicativo favorito selecionado."
            } else {
                "Nenhum favorito encontrado."
            }
            AureoleText(
                text = emptyMsg,
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        itemsIndexed(
            items = filteredFavorites,
            key = { _, app -> "fav_${app.packageName}" }
        ) { index, app ->
            FavoriteAppRow(
                params = FavoriteAppRowParams(
                    app = app,
                    index = index,
                    totalCount = favoriteCount,
                    canReorder = searchQuery.isBlank()
                ),
                onMoveUp = { onMoveUp(index) },
                onMoveDown = { onMoveDown(index) },
                onToggleFavorite = { onToggleFavorite(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.remainingSectionItems(
    filteredRemaining: List<AppInfo>,
    searchQuery: String,
    onToggleFavorite: (String) -> Unit,
    onActivateAll: () -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        RemainingSectionHeader(
            count = filteredRemaining.size,
            onActivateAll = onActivateAll
        )
    }

    if (filteredRemaining.isEmpty()) {
        item {
            val emptyMsg = if (searchQuery.isBlank()) {
                "Todos os aplicativos já estão nos favoritos."
            } else {
                "Nenhum outro aplicativo encontrado."
            }
            AureoleText(
                text = emptyMsg,
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        itemsIndexed(
            items = filteredRemaining,
            key = { _, app -> "rem_${app.packageName}" }
        ) { _, app ->
            RemainingAppRow(
                app = app,
                onToggleFavorite = { onToggleFavorite(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun FavoriteAppRow(
    params: FavoriteAppRowParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FavoriteAppRowInfo(app = params.app, modifier = Modifier.weight(1f))

        FavoriteAppRowActionButtons(
            params = params,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onToggleFavorite = onToggleFavorite
        )
    }
}

@Composable
private fun FavoriteAppsSearchInput(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            AureoleText(
                "Buscar aplicativo...",
                color = AureoleTheme.colors.onSurfaceLow
            )
        },
        leadingIcon = {
            AureoleDS.icons.Search(
                tint = AureoleTheme.colors.onSurfaceMedium
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = AureoleTheme.colors.outline,
            focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
fun FavoriteAppsDialogPreview() {
    val config = FavoriteAppsDialogConfig(
        allApps = emptyList(),
        favoriteAppPackages = emptyList()
    )
    val actions = FavoriteAppsDialogActions(onDismiss = {})
    AureoleLauncherTheme {
        FavoriteAppsDialog(config = config, actions = actions)
    }
}
