@file:Suppress("TooManyFunctions")

package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.bottomsheet.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppRowParams
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsBodyParams
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteSectionParams
import kotlinx.coroutines.delay

enum class FavoriteAppsScreenMode {
    MAIN, EDIT_SHORTCUTS, APPEARANCE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAppsDialog(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    var isVisible by remember { mutableStateOf(true) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(300L)
            actions.onDismiss()
        }
    }

    var currentMode by remember { mutableStateOf(FavoriteAppsScreenMode.MAIN) }

    val appMap = remember(config.allApps) {
        config.allApps.associateBy { it.packageName }
    }

    val previewFavoriteApps = remember(config.favoriteAppPackages, appMap) {
        config.favoriteAppPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    val titleText = when (currentMode) {
        FavoriteAppsScreenMode.MAIN -> "Lista de Aplicativos"
        FavoriteAppsScreenMode.EDIT_SHORTCUTS -> "Edit shortcuts"
        FavoriteAppsScreenMode.APPEARANCE -> "Aparência"
    }

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        title = titleText,
        isVisible = isVisible,
        animated = true,
        maxSheetHeightFraction = 0.65f,
        onBackPress = {
            if (currentMode != FavoriteAppsScreenMode.MAIN) {
                currentMode = FavoriteAppsScreenMode.MAIN
            } else {
                isVisible = false
            }
        },
        previewContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
                    .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.35f))
                    .border(
                        width = AureoleDS.dimens.borderMinimal,
                        color = AureoleTheme.colors.outline.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
                    )
                    .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xxSmall),
                contentAlignment = Alignment.Center
            ) {
                if (previewFavoriteApps.isEmpty()) {
                    AureoleText(
                        text = "Nenhum favorito selecionado",
                        style = AureoleTheme.typography.bodyMedium,
                        color = AureoleTheme.colors.onSurfaceMedium,
                        modifier = Modifier.padding(AureoleDS.dimens.medium)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(previewFavoriteApps) { app ->
                            AppItemRow(
                                app = app,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        },
        bottomBarContent = {
            Button(
                onClick = {
                    if (currentMode != FavoriteAppsScreenMode.MAIN) {
                        currentMode = FavoriteAppsScreenMode.MAIN
                    } else {
                        isVisible = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                AureoleText(if (currentMode == FavoriteAppsScreenMode.MAIN) "Concluído" else "Voltar")
            }
        }
    ) {
        when (currentMode) {
            FavoriteAppsScreenMode.MAIN -> {
                MainFavoritesContent(
                    onEditShortcutsClick = { currentMode = FavoriteAppsScreenMode.EDIT_SHORTCUTS },
                    onAppearanceClick = { currentMode = FavoriteAppsScreenMode.APPEARANCE }
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
}

@Composable
private fun MainFavoritesContent(
    onEditShortcutsClick: () -> Unit,
    onAppearanceClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditShortcutsClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Star(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Edit shortcuts",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
            Text(
                text = "Gerenciar aplicativos e ordem",
                style = AureoleTheme.typography.bodySmall,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAppearanceClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Edit(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Appearance",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
            Text(
                text = "Exibição dos apps na Home",
                style = AureoleTheme.typography.bodySmall,
                color = AureoleTheme.colors.onSurfaceMedium
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
        FavoriteAppsSearchInput(
            query = params.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
        placeholder = { AureoleText("Buscar aplicativo...") },
        leadingIcon = {
            AureoleDS.icons.Search(
                tint = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = AureoleTheme.colors.outline.copy(alpha = 0.5f),
            focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
