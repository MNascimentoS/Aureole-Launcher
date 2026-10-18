package dev.mnascimentos.aureole.feature.home.components.favorites.model

import android.appwidget.AppWidgetHost
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.feature.home.components.favorites.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

data class ScrollableFavoritesParams(
    val config: FavoritesListConfig,
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val favoritePackages: Set<String>,
    val appWidgetHost: AppWidgetHost? = null,
    val showHeadersAndWidgets: Boolean,
    val modifier: Modifier
)
