package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.feature.settings.SettingsViewModel

fun SettingsViewModel.toggleFavorite(packageName: String) {
    val current = uiState.value.favoriteAppPackages.toMutableList()
    if (current.contains(packageName)) {
        current.remove(packageName)
    } else {
        current.add(packageName)
    }
    settingsRepository.favoriteAppPackages = current
    updateUiState { it.copy(favoriteAppPackages = current) }
}
