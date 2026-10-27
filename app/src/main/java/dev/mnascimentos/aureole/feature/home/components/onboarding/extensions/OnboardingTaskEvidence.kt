package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal fun MainUiState.hasFavoriteApps(): Boolean =
    favoriteAppPackages.isNotEmpty() || containerFavorites.values.any { it.isNotEmpty() }

internal fun MainUiState.hasFilledContainer(): Boolean =
    containers.values.any { it.items.isNotEmpty() || it.folders.isNotEmpty() }

internal fun MainUiState.hasWidgets(): Boolean =
    topWidgetIds.isNotEmpty() ||
        widgetStacks.values.any { it.isNotEmpty() } ||
        gridItems.any { it.hasValidWidget() }

private fun LauncherItemState.hasValidWidget(): Boolean {
    val isBoundWidget = safeType == LauncherItemType.SINGLE_APP_WIDGET && (widgetId ?: 0) > 0
    return isBoundWidget || safeChildren.any { it.hasValidWidget() }
}
