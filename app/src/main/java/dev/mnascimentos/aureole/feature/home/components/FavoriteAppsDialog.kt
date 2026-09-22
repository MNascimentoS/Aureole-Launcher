package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppInfo

private const val DIALOG_WIDTH_FRACTION = 0.92f

data class FavoriteAppsDialogConfig(
    val allApps: List<AppInfo>,
    val favoriteAppPackages: List<String>,
    val containerId: String? = null,
    val showAllAppsOnHome: Boolean = true
)

data class FavoriteAppsDialogActions(
    val onToggleFavorite: (String) -> Unit = {},
    val onUpdateFavoritePackages: (String?, List<String>) -> Unit = { _, _ -> },
    val onToggleShowAllAppsOnHome: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)

data class FavoriteAppRowParams(
    val app: AppInfo,
    val index: Int,
    val totalCount: Int,
    val canReorder: Boolean
)

data class FavoriteSectionParams(
    val filteredFavorites: List<AppInfo>,
    val searchQuery: String,
    val favoriteCount: Int
)

data class FavoriteAppsBodyParams(
    val config: FavoriteAppsDialogConfig,
    val actions: FavoriteAppsDialogActions,
    val searchQuery: String,
    val favoriteCount: Int,
    val filteredFavorites: List<AppInfo>,
    val filteredRemaining: List<AppInfo>
)

@Composable
fun FavoriteAppsDialog(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions
) {
    Dialog(
        onDismissRequest = actions.onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .heightIn(max = 620.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            FavoriteAppsDialogContent(config = config, actions = actions)
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

    Column(modifier = Modifier.fillMaxWidth()) {
        FavoriteAppsDialogHeader(onDismiss = actions.onDismiss, containerId = config.containerId)
        Spacer(modifier = Modifier.height(12.dp))

        if (config.containerId == null) {
            FavoriteAppsShowAllSwitchRow(
                showAllAppsOnHome = config.showAllAppsOnHome,
                onToggleShowAllAppsOnHome = actions.onToggleShowAllAppsOnHome
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = params.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar aplicativo...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
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
                onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) }
            )

            remainingSectionItems(
                filteredRemaining = params.filteredRemaining,
                searchQuery = params.searchQuery,
                onToggleFavorite = { pkg -> actions.onToggleFavorite(pkg) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = actions.onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text("Concluído")
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

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.favoriteSectionItems(
    params: FavoriteSectionParams,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val filteredFavorites = params.filteredFavorites
    val searchQuery = params.searchQuery
    val favoriteCount = params.favoriteCount

    item {
        Text(
            text = "Favoritos (${filteredFavorites.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    if (filteredFavorites.isEmpty()) {
        item {
            val emptyMsg = if (searchQuery.isBlank()) {
                "Nenhum aplicativo favorito selecionado."
            } else {
                "Nenhum favorito encontrado."
            }
            Text(
                text = emptyMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    onToggleFavorite: (String) -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Outros Aplicativos (${filteredRemaining.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    if (filteredRemaining.isEmpty()) {
        item {
            val emptyMsg = if (searchQuery.isBlank()) {
                "Todos os aplicativos já estão nos favoritos."
            } else {
                "Nenhum outro aplicativo encontrado."
            }
            Text(
                text = emptyMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
