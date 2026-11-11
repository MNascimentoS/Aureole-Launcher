package dev.mnascimentos.aureole.composable.favorites.model

import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo

enum class FavoriteAppsScreenMode {
    MAIN, EDIT_SHORTCUTS, APPEARANCE
}

data class FavoriteAppsDialogConfig(
    val allApps: List<AppInfo>,
    val favoriteAppPackages: List<String>,
    val containerId: String? = null,
    val rowConfig: AppItemRowConfig = AppItemRowConfig(),
    /** The apps have not arrived yet: the list shows its skeleton. */
    val isLoading: Boolean = false
)

data class FavoriteAppsDialogActions(
    val onToggleFavorite: (String) -> Unit = {},
    val onUpdateFavoritePackages: (String?, List<String>) -> Unit = { _, _ -> },
    val onToggleShowAllAppsOnHome: () -> Unit = {},
    val onSettingsClick: (() -> Unit)? = null,
    val onDismiss: () -> Unit = {}
)

/** Favorites shown in the manage list; they can only be reordered while [searchQuery] is blank. */
data class FavoriteSectionParams(
    val filteredFavorites: List<AppInfo>,
    val searchQuery: String
)

/**
 * Callbacks of the favorites section. [onMove] is one swap of the reorderable list (apply it to a local draft) and
 * [onMoveEnd] the end of the gesture, where the new order is saved.
 */
data class FavoriteSectionActions(
    val onMove: (from: Int, to: Int) -> Unit,
    val onMoveEnd: () -> Unit,
    val onToggleFavorite: (String) -> Unit,
    val onDeactivateAll: () -> Unit
)

data class FavoriteAppsBodyParams(
    val config: FavoriteAppsDialogConfig,
    val actions: FavoriteAppsDialogActions,
    val searchQuery: String,
    val filteredFavorites: List<AppInfo>,
    val filteredRemaining: List<AppInfo>
)
