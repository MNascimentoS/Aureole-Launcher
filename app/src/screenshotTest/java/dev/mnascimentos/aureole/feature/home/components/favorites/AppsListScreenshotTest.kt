package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesPreviewMetrics
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

private const val LIST_ID = "apps_list"
private const val LIST_HEIGHT_PX = 400f

/** The apps list of the home with [favoriteCount] of the sample apps as its favorites. */
@Composable
private fun AppsListCase(favoriteCount: Int, showAllApps: Boolean) {
    val sample = createSampleHomeData()
    val favorites = sample.uiState.favoriteAppPackages.take(favoriteCount)
    val uiState = sample.uiState.copy(containerFavorites = mapOf(LIST_ID to favorites))
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides uiState.isLeftHandedMode,
            LocalHomeActions provides sample.actions
        ) {
            AppsList(
                config = FavoritesListConfig(
                    currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                    currentHeightPx = LIST_HEIGHT_PX,
                    state = rememberLazyListState()
                ),
                options = FavoritesListOptions(
                    containerId = LIST_ID,
                    showHeadersAndWidgets = false,
                    showAllApps = showAllApps
                )
            )
        }
    }
}

/** Toggle on with favorites: "Favorites" on top, then "All apps" with the rest, no app repeated. */
@PreviewTest
@AureolePreview
@Composable
private fun AppsListAllAppsWithFavoritesScreenshotTest() = AppsListCase(favoriteCount = 2, showAllApps = true)

/** Toggle on without favorites (a new list): only the "All apps" label and every app. */
@PreviewTest
@AureolePreview
@Composable
private fun AppsListAllAppsWithoutFavoritesScreenshotTest() = AppsListCase(favoriteCount = 0, showAllApps = true)

/** Toggle off (a favorites list from 0.9.30): only the favorites, no labels, as before. */
@PreviewTest
@AureolePreview
@Composable
private fun AppsListFavoritesOnlyScreenshotTest() = AppsListCase(favoriteCount = 2, showAllApps = false)
