package dev.mnascimentos.aureole.core.data.model

/**
 * The apps the Home lists. With no [favorites] configured it lists every app of [allApps]; otherwise only the
 * favorites, in their saved order. A favorite that is no longer installed is skipped.
 */
fun visibleApps(allApps: List<AppInfo>, favorites: List<String>): List<AppInfo> {
    if (favorites.isEmpty()) return allApps
    val byPackage = allApps.associateBy { it.packageName }
    return favorites.mapNotNull { byPackage[it] }
}
