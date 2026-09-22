package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview

private const val FAVORITE_DIALOG_HEIGHT_FRACTION = 0.85f
private val APP_ICON_SIZE = 36.dp

@Composable
fun FavoriteAppsDialog(
    allApps: List<AppInfo>,
    favoriteAppPackages: List<String>,
    containerId: String? = null,
    showAllAppsOnHome: Boolean = true,
    onToggleFavorite: (String) -> Unit = {},
    onUpdateFavoritePackages: (String?, List<String>) -> Unit = { _, _ -> },
    onToggleShowAllAppsOnHome: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val appMap = remember(allApps) {
        allApps.associateBy { it.packageName }
    }

    // Map ordered favorite packages to AppInfo list
    val favoriteApps = remember(favoriteAppPackages, appMap) {
        favoriteAppPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    // Remaining non-favorite apps
    val remainingApps = remember(allApps, favoriteAppPackages) {
        val favSet = favoriteAppPackages.toSet()
        allApps.filter { it.packageName !in favSet }
            .sortedBy { it.label.lowercase() }
    }

    // Filter by search query
    val filteredFavorites = remember(searchQuery, favoriteApps) {
        if (searchQuery.isBlank()) {
            favoriteApps
        } else {
            favoriteApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    val filteredRemaining = remember(searchQuery, remainingApps) {
        if (searchQuery.isBlank()) {
            remainingApps
        } else {
            remainingApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    val handleMoveUp = { index: Int ->
        if (index > 0 && index < favoriteAppPackages.size) {
            val list = favoriteAppPackages.toMutableList()
            val item = list.removeAt(index)
            list.add(index - 1, item)
            onUpdateFavoritePackages(containerId, list)
        }
    }

    val handleMoveDown = { index: Int ->
        if (index >= 0 && index < favoriteAppPackages.size - 1) {
            val list = favoriteAppPackages.toMutableList()
            val item = list.removeAt(index)
            list.add(index + 1, item)
            onUpdateFavoritePackages(containerId, list)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxHeight(FAVORITE_DIALOG_HEIGHT_FRACTION)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            FavoriteAppsHeader(onDismiss = onDismiss)

            Spacer(modifier = Modifier.height(12.dp))

            // Container settings: Toggle Show All Apps on Home
            ShowAllAppsSettingRow(
                showAllAppsOnHome = showAllAppsOnHome,
                onToggleShowAllAppsOnHome = onToggleShowAllAppsOnHome
            )

            Spacer(modifier = Modifier.height(12.dp))

            FavoriteAppsSearchField(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Section 1: Favorites Pinned at Top (RF04 & RF05)
                item(key = "header_favorites") {
                    Text(
                        text = "Favoritos (${favoriteApps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (filteredFavorites.isEmpty()) {
                    item(key = "empty_favorites") {
                        Text(
                            text = if (searchQuery.isBlank()) "Nenhum aplicativo favorito selecionado." else "Nenhum favorito encontrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                } else {
                    itemsIndexed(
                        items = filteredFavorites,
                        key = { _, app -> "fav_${app.packageName}" }
                    ) { index, app ->
                        FavoriteAppRow(
                            app = app,
                            index = index,
                            totalCount = filteredFavorites.size,
                            canReorder = searchQuery.isBlank(),
                            onMoveUp = { handleMoveUp(index) },
                            onMoveDown = { handleMoveDown(index) },
                            onToggleFavorite = {
                                onToggleFavorite(app.packageName)
                            }
                        )
                    }
                }

                item(key = "divider_sections") {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Section 2: Other Installed Apps
                item(key = "header_remaining") {
                    Text(
                        text = "Outros Aplicativos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (filteredRemaining.isEmpty()) {
                    item(key = "empty_remaining") {
                        Text(
                            text = if (searchQuery.isBlank()) "Todos os aplicativos já estão nos favoritos." else "Nenhum outro aplicativo encontrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                } else {
                    itemsIndexed(
                        items = filteredRemaining,
                        key = { _, app -> "rem_${app.packageName}" }
                    ) { _, app ->
                        NonFavoriteAppRow(
                            app = app,
                            onToggleFavorite = {
                                onToggleFavorite(app.packageName)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FavoriteAppsFooter(onDismiss = onDismiss)
        }
    }
}

@Composable
private fun FavoriteAppsHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Configurar Favoritos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fechar"
            )
        }
    }
}

@Composable
private fun ShowAllAppsSettingRow(
    showAllAppsOnHome: Boolean,
    onToggleShowAllAppsOnHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onToggleShowAllAppsOnHome)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mostrar todos os apps na home",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Exibe lista completa abaixo dos favoritos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = showAllAppsOnHome,
            onCheckedChange = { onToggleShowAllAppsOnHome() }
        )
    }
}

@Composable
private fun FavoriteAppsSearchField(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar aplicativos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FavoriteAppRow(
    app: AppInfo,
    index: Int,
    totalCount: Int,
    canReorder: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            .clickable(onClick = onToggleFavorite)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canReorder) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = index > 0,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Mover para cima",
                        tint = if (index > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(
                                alpha = 0.3f
                            )
                        }
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = index < totalCount - 1,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Mover para baixo",
                        tint = if (index < totalCount - 1) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(
                                alpha = 0.3f
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(APP_ICON_SIZE)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = true,
            onCheckedChange = { onToggleFavorite() }
        )
    }
}

@Composable
private fun NonFavoriteAppRow(
    app: AppInfo,
    onToggleFavorite: () -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggleFavorite)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(APP_ICON_SIZE)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = false,
            onCheckedChange = { onToggleFavorite() }
        )
    }
}

@Composable
private fun FavoriteAppsFooter(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onDismiss) {
            Text("Concluído")
        }
    }
}

@AureolePreview
@Composable
fun FavoriteAppsDialogPreview() {
    val mockApp = AppInfo(
        label = "Mensagens",
        packageName = "com.example.messages",
        componentName = ComponentName("com.example.messages", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    AureoleLauncherTheme {
        FavoriteAppsDialog(
            allApps = listOf(mockApp),
            favoriteAppPackages = listOf(mockApp.packageName),
            showAllAppsOnHome = true,
            onToggleFavorite = {},
            onUpdateFavoritePackages = { _, _ -> },
            onToggleShowAllAppsOnHome = {},
            onDismiss = {}
        )
    }
}
