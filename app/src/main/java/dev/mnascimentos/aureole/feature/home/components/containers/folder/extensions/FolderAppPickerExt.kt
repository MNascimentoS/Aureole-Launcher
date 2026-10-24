package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import dev.mnascimentos.aureole.core.data.model.AppInfo

fun filterAppsBySearch(apps: List<AppInfo>, query: String): List<AppInfo> {
    return if (query.isBlank()) {
        apps
    } else {
        apps.filter { it.label.contains(query, ignoreCase = true) }
    }
}

fun togglePackageSelection(selectedPackages: MutableList<String>, pkg: String) {
    if (selectedPackages.contains(pkg)) {
        selectedPackages.remove(pkg)
    } else {
        selectedPackages.add(pkg)
    }
}
