package dev.mnascimentos.aureole.composable.favorites.model

import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.reorder.model.ReorderAutoScroll
import dev.mnascimentos.aureole.core.data.model.AppInfo

/** The "Show all apps" switch of a home apps list: whether it is on and what flips it. */
data class ShowAllAppsParams(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

/** The "Show labels" switch of a home apps list: whether it is on and what flips it. */
data class ShowLabelsParams(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

data class FavoriteAppsDialogConfig(
    val allApps: List<AppInfo>,
    val favoriteAppPackages: List<String>,
    val containerId: String? = null,
    val rowConfig: AppItemRowConfig = AppItemRowConfig(),
    /** The apps have not arrived yet: the list shows its skeleton. */
    val isLoading: Boolean = false,
    /** The "Show all apps" switch of a home apps list; null (Settings) leaves the menu without it. */
    val showAllApps: ShowAllAppsParams? = null,
    /** The "Show labels" switch of a home apps list; null leaves the menu without it. */
    val showLabels: ShowLabelsParams? = null
)

data class FavoriteAppsDialogActions(
    val onToggleFavorite: (String) -> Unit = {},
    val onUpdateFavoritePackages: (String?, List<String>) -> Unit = { _, _ -> },
    val onSettingsClick: (() -> Unit)? = null,
    /** Pencil "Customize home" at the start of the title line; null (Settings, drawer favorites) leaves it out. */
    val onCustomizeHome: (() -> Unit)? = null,
    val onDismiss: () -> Unit = {}
)

/** Favorites shown in the manage list; they can only be reordered while [searchQuery] is blank. */
data class FavoriteSectionParams(
    val filteredFavorites: List<AppInfo>,
    val searchQuery: String,
    /** Favorites before the search filter; the reorder hint of the header needs two. */
    val favoritesCount: Int = filteredFavorites.size,
    /** Auto scroll of the list around the favorites, so a held favorite near its edges scrolls it. */
    val autoScroll: ReorderAutoScroll? = null
)

/**
 * Callbacks of the favorites section. [onMove] is one swap of the reorderable list (apply it to a local draft) and
 * [onMoveEnd] the end of the gesture, where the new order is saved.
 */
data class FavoriteSectionActions(
    val onMove: (from: Int, to: Int) -> Unit,
    val onMoveEnd: () -> Unit,
    val onToggleFavorite: (String) -> Unit,
    val onRemoveAll: () -> Unit
)

data class FavoriteAppsBodyParams(
    val config: FavoriteAppsDialogConfig,
    val actions: FavoriteAppsDialogActions,
    val searchQuery: String,
    val filteredFavorites: List<AppInfo>,
    val filteredRemaining: List<AppInfo>
)

/** What the preview frame of "Choose favorites" draws: the list as the Home shows it. */
data class FavoriteAppsPreviewParams(
    val sections: AppsListSections,
    /** Section labels on, as on the Home: only with "Show all apps" on and "Show labels" on. */
    val hasLabels: Boolean,
    val rowConfig: AppItemRowConfig
)
