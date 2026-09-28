package dev.mnascimentos.aureole.feature.home.components.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

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
