package dev.mnascimentos.aureole.feature.home.components.favorites.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

/** How many of the other apps an apps list inside a scroll view shows: it cannot scroll on its own. */
const val APPS_LIST_MAX_OTHERS_IN_SCROLL_VIEW = 20

/**
 * What an apps list shows: its [favorites] on top, in their saved order, then the [others] when it [showsAll]
 * apps. The section labels ("Favorites" and "All apps") show only when it shows all apps.
 */
data class AppsListSections(
    val favorites: List<AppInfo> = emptyList(),
    val others: List<AppInfo> = emptyList(),
    val showsAll: Boolean = false
) {
    /** Nothing to list: no favorite installed and, when it shows all apps, no app at all. */
    val isEmpty: Boolean
        get() = favorites.isEmpty() && others.isEmpty()
}

/**
 * The sections of an apps list over the installed [apps] (already in alphabetical order): the [favorites] that are
 * still installed, in their saved order, and, with [showAll], every other app in the order of [apps], without
 * repeating a favorite. At most [maxOthers] other apps.
 */
fun appsListSections(
    apps: List<AppInfo>,
    favorites: List<String>,
    showAll: Boolean,
    maxOthers: Int = Int.MAX_VALUE
): AppsListSections {
    val byPackage = apps.associateBy { it.packageName }
    val favoriteApps = favorites.distinct().mapNotNull { byPackage[it] }
    val favoritePackages = favorites.toSet()
    val others = if (showAll) {
        apps.asSequence().filterNot { it.packageName in favoritePackages }.take(maxOthers).toList()
    } else {
        emptyList()
    }
    return AppsListSections(favorites = favoriteApps, others = others, showsAll = showAll)
}
