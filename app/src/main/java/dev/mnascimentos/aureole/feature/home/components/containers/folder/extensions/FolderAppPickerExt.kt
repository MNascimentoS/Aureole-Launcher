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

/** Moves the package at [index] one place up; out of range or already first changes nothing. */
fun MutableList<String>.moveUp(index: Int) {
    if (index > 0 && index < size) add(index - 1, removeAt(index))
}

/** Moves the package at [index] one place down; out of range or already last changes nothing. */
fun MutableList<String>.moveDown(index: Int) {
    if (index >= 0 && index < size - 1) add(index + 1, removeAt(index))
}
