package dev.mnascimentos.aureole.feature.home.components.favorites.model

import android.appwidget.AppWidgetHost
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The apps list that scrolls on its own, optionally under the clock header and the widget row. */
data class ScrollableAppsListParams(
    val config: FavoritesListConfig,
    val uiState: MainUiState,
    val list: AppsListParams,
    val appWidgetHost: AppWidgetHost? = null,
    val showHeadersAndWidgets: Boolean,
    val modifier: Modifier
)
