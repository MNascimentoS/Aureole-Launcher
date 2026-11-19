package dev.mnascimentos.aureole.composable.favorites.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

/** The two parts of the "Choose favorites" list: the favorites in their order and the other apps A–Z. */
data class FavoriteAppsLists(
    val favorites: List<AppInfo>,
    val remaining: List<AppInfo>
)

/**
 * Splits [allApps] into the favorites, in the order of [favoritePackages] (packages no longer installed are left
 * out), and the remaining apps sorted by name.
 */
fun favoriteAppsLists(allApps: List<AppInfo>, favoritePackages: List<String>): FavoriteAppsLists {
    val byPackage = allApps.associateBy { it.packageName }
    val favoriteSet = favoritePackages.toSet()
    return FavoriteAppsLists(
        favorites = favoritePackages.mapNotNull { byPackage[it] },
        remaining = allApps.filter { it.packageName !in favoriteSet }.sortedBy { it.label.lowercase() }
    )
}

/** The apps whose name contains [query], ignoring case; a blank query keeps them all (RF-04). */
fun filterAppsByQuery(apps: List<AppInfo>, query: String): List<AppInfo> {
    if (query.isBlank()) return apps
    return apps.filter { it.label.contains(query, ignoreCase = true) }
}

/** [filterAppsByQuery] over both parts, so the search finds favorites and the other apps alike. */
fun FavoriteAppsLists.filteredBy(query: String): FavoriteAppsLists = FavoriteAppsLists(
    favorites = filterAppsByQuery(favorites, query),
    remaining = filterAppsByQuery(remaining, query)
)

/** The one-line notice of the list without favorites (RF-03): what the Home shows meanwhile. */
enum class FavoritesEmptyNotice {
    /** "Show all apps" on (or no switch, as in Settings): the Home lists every app. */
    HOME_SHOWS_ALL,

    /** "Show all apps" off: the list stays empty on the Home. */
    HOME_STAYS_EMPTY
}

/**
 * The notice for [favoriteCount] favorites: only with none and outside a search. [showAllApps] is the list's switch,
 * or null when the sheet has none (Settings), where the Home falls back to every app.
 */
fun favoritesEmptyNotice(favoriteCount: Int, showAllApps: Boolean?, isSearching: Boolean): FavoritesEmptyNotice? =
    when {
        favoriteCount > 0 || isSearching -> null
        showAllApps == false -> FavoritesEmptyNotice.HOME_STAYS_EMPTY
        else -> FavoritesEmptyNotice.HOME_SHOWS_ALL
    }
