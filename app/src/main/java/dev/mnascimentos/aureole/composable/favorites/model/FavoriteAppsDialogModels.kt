package dev.mnascimentos.aureole.composable.favorites.model

import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.reorder.model.ReorderAutoScroll
import dev.mnascimentos.aureole.core.data.model.AppInfo

/** The "Show all apps" switch of a home apps list: whether it is on and what flips it. */
data class ShowAllAppsParams(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

/** What the subtitle of "Choose favorites" says about the Home. */
enum class FavoritesMenuSubtitle {
    /** No favorites: the Home lists every app. */
    ALL_SHOWN,

    /** No favorites and the list shows only them: nothing is picked yet. */
    NONE_SELECTED,

    /** "N favorites, the rest stays hidden". */
    COUNT,

    /** "N favorites on top, then all apps". */
    COUNT_ON_TOP
}

/**
 * The subtitle of "Choose favorites" for [favoriteCount] favorites. [showAllApps] is the list's switch, or null when
 * the sheet has none (the global favorites in Settings), which keeps the old wording.
 */
fun favoritesMenuSubtitle(favoriteCount: Int, showAllApps: Boolean?): FavoritesMenuSubtitle = when {
    favoriteCount == 0 && showAllApps == false -> FavoritesMenuSubtitle.NONE_SELECTED
    favoriteCount == 0 -> FavoritesMenuSubtitle.ALL_SHOWN
    showAllApps == true -> FavoritesMenuSubtitle.COUNT_ON_TOP
    else -> FavoritesMenuSubtitle.COUNT
}

data class FavoriteAppsDialogConfig(
    val allApps: List<AppInfo>,
    val favoriteAppPackages: List<String>,
    val containerId: String? = null,
    val rowConfig: AppItemRowConfig = AppItemRowConfig(),
    /** The apps have not arrived yet: the list shows its skeleton. */
    val isLoading: Boolean = false,
    /** The "Show all apps" switch of a home apps list; null (Settings) leaves the menu without it. */
    val showAllApps: ShowAllAppsParams? = null
)

data class FavoriteAppsDialogActions(
    val onToggleFavorite: (String) -> Unit = {},
    val onUpdateFavoritePackages: (String?, List<String>) -> Unit = { _, _ -> },
    val onSettingsClick: (() -> Unit)? = null,
    /** "Customize home" row at the top of the list; null (Settings, drawer favorites) leaves it out. */
    val onCustomizeHome: (() -> Unit)? = null,
    val onDismiss: () -> Unit = {}
)

/** Favorites shown in the manage list; they can only be reordered while [searchQuery] is blank. */
data class FavoriteSectionParams(
    val filteredFavorites: List<AppInfo>,
    val searchQuery: String,
    /** Favorites before the search filter; the drag hint needs two, the empty banner none. */
    val favoritesCount: Int = filteredFavorites.size,
    /** Apps the Home lists while there are no favorites. */
    val allAppsCount: Int = 0,
    /** The Home lists every app while there are no favorites, so the empty banner says so; off with the switch off. */
    val isShowingAllAppsWhenEmpty: Boolean = true,
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
