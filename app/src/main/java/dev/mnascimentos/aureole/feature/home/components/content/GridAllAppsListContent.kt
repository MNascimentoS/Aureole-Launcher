package dev.mnascimentos.aureole.feature.home.components.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.feature.home.components.allapps.AllAppsList
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams

@Composable
fun GridAllAppsListContent(
    params: GridItemContentParams,
    modifier: Modifier = Modifier
) {
    val isInScrollView = params.isInScrollView
    val listModifier = if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize()
    Box(modifier = modifier.then(listModifier)) {
        AllAppsList(
            config = if (isInScrollView) params.favConfig.copy(hazeState = null) else params.favConfig,
            appWidgetHost = params.appWidgetHost,
            options = FavoritesListOptions(
                containerId = params.item.id,
                showHeadersAndWidgets = false,
                isInsideScrollView = isInScrollView
            ),
            modifier = listModifier
        )
    }
}
