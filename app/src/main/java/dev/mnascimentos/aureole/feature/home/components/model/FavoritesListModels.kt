package dev.mnascimentos.aureole.feature.home.components.model

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

data class FavoritesListOptions(
    val containerId: String? = null,
    val showHeadersAndWidgets: Boolean = true,
    val isInsideScrollView: Boolean = false
)

data class NonScrollableFavoritesParams(
    val uiState: MainUiState,
    val favoriteApps: List<AppInfo>,
    val actions: HomeScreenActions,
    val favoritePackages: Set<String>,
    val containerId: String? = null
)
