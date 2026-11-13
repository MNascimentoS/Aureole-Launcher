package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.search.rankByQuery

/** Apps that match [query], best first. A blank query gives an empty list: there is no search. */
fun searchFolderApps(apps: List<AppInfo>, query: String): List<AppInfo> {
    if (query.isBlank()) return emptyList()
    return rankByQuery(apps, query) { it.label }
}

/** Apps of the folder in the order of [selectedPackages], skipping packages that are not installed. */
fun selectedTrayApps(selectedPackages: List<String>, appsByPackage: Map<String, AppInfo>): List<AppInfo> =
    selectedPackages.mapNotNull { appsByPackage[it] }

/**
 * What Enter does in the picker: marks or unmarks the best of [results]. Returns the package that was
 * toggled, or null when there was no result.
 */
fun toggleBestSearchResult(selectedPackages: MutableList<String>, results: List<AppInfo>): String? {
    val best = results.firstOrNull() ?: return null
    togglePackageSelection(selectedPackages, best.packageName)
    return best.packageName
}

fun togglePackageSelection(selectedPackages: MutableList<String>, pkg: String) {
    if (selectedPackages.contains(pkg)) {
        selectedPackages.remove(pkg)
    } else {
        selectedPackages.add(pkg)
    }
}

/**
 * Applies a swap of the tray to [selectedPackages]: the app at [from] of [trayPackages] takes the place of the
 * app at [to]. The tray skips packages that are not installed, so its indexes are mapped to the selection
 * through the packages. Out of range or the same place changes nothing.
 */
fun moveTrayApp(selectedPackages: MutableList<String>, trayPackages: List<String>, from: Int, to: Int) {
    val fromIndex = trayPackages.getOrNull(from)?.let(selectedPackages::indexOf) ?: -1
    val toIndex = trayPackages.getOrNull(to)?.let(selectedPackages::indexOf) ?: -1
    if (fromIndex < 0 || toIndex < 0 || fromIndex == toIndex) return
    selectedPackages.add(toIndex, selectedPackages.removeAt(fromIndex))
}
