package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppRowParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteSectionParams

@Composable
internal fun FavoriteSectionHeader(
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
internal fun RemainingSectionHeader(
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
internal fun LazyListScope.favoriteSectionItems(
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
            key = { _, app -> "fav_${app.packageName}" },
            contentType = { _, _ -> "fav_app_row" }
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
internal fun LazyListScope.remainingSectionItems(
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

@Composable
internal fun FavoriteAppRow(
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
internal fun FavoriteAppsSearchInput(
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
