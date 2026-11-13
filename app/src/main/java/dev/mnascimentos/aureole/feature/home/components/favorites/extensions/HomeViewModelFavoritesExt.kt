package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions.updateAppsState
import dev.mnascimentos.aureole.feature.home.components.favorites.model.toggleFavoriteIn
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import dev.mnascimentos.aureole.feature.home.model.favoritesOf
import dev.mnascimentos.aureole.feature.home.model.isLegacyFavoritesId
import kotlinx.coroutines.launch
import timber.log.Timber

/** Opens the favorites sheet of [containerId] on [initialMode], or closes it. */
fun HomeViewModel.setShowFavoritePicker(
    show: Boolean,
    containerId: String? = null,
    initialMode: FavoriteAppsScreenMode = FavoriteAppsScreenMode.MAIN
) {
    updateUiState {
        it.copy(
            showFavoritePickerDialog = show,
            activeFavoriteContainerId = if (show) (containerId ?: it.activeFavoriteContainerId) else null,
            favoritePickerInitialMode = if (show) initialMode else FavoriteAppsScreenMode.MAIN
        )
    }
}

fun HomeViewModel.toggleFavorite(packageName: String, containerId: String? = null) {
    val targetContainerId = containerId ?: uiState.value.activeFavoriteContainerId ?: "default_favorites"
    val previousFavs = uiState.value.favoritesOf(targetContainerId)
    val toggle = toggleFavoriteIn(previousFavs, packageName)
    val appName = uiState.value.apps.find { it.packageName == packageName }?.label ?: packageName
    updateContainerFavorites(targetContainerId, toggle.favorites)
    showUndo(TextResource.fromResId(toggle.undoMessageRes, appName)) {
        updateContainerFavorites(targetContainerId, previousFavs)
    }
}

fun HomeViewModel.updateFavoritePackages(newFavorites: List<String>) {
    val targetContainerId = uiState.value.activeFavoriteContainerId ?: "default_favorites"
    updateContainerFavorites(targetContainerId, newFavorites)
}

fun HomeViewModel.updateContainerFavorites(containerId: String, newFavorites: List<String>) {
    viewModelScope.launch {
        runCatching {
            favoriteContainerRepository.updateContainerFavorites(containerId, newFavorites)
        }.onFailure { e ->
            e.printStackTrace()
            Timber.e(e)
        }
        val updatedMap = uiState.value.containerFavorites.toMutableMap()
        updatedMap[containerId] = newFavorites
        // The global favorites follow the legacy list only; another list must not overwrite them.
        val isLegacy = isLegacyFavoritesId(containerId)
        if (isLegacy) settingsRepository.favoriteAppPackages = newFavorites
        updateUiState {
            it.copy(
                favoriteAppPackages = if (isLegacy) newFavorites else it.favoriteAppPackages,
                containerFavorites = updatedMap
            )
        }
        updateAppsState(uiState.value.apps)
    }
}
