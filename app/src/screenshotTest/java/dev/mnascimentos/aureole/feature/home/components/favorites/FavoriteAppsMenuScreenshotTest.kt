package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsMainContent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

@Composable
private fun FavoritesMenuDialogCase(isLeftHanded: Boolean, showAllApps: Boolean) {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState.copy(isLeftHandedMode = isLeftHanded),
            LocalLeftHanded provides (sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)).isLeftHandedMode
        ) {
            FavoriteAppsDialog(
                config = FavoriteAppsDialogConfig(
                    allApps = sampleData.apps,
                    favoriteAppPackages = sampleData.apps.take(5).map { it.packageName },
                    rowConfig = AppItemRowConfig(isLeftHandedMode = isLeftHanded),
                    showAllApps = ShowAllAppsParams(checked = showAllApps, onCheckedChange = {})
                ),
                actions = FavoriteAppsDialogActions(onCustomizeHome = {})
            )
        }
    }
}

/** Menu of a home apps list with "Show all apps" on: "Choose favorites" says the favorites sit on top. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuRightHandedScreenshotTest() =
    FavoritesMenuDialogCase(isLeftHanded = false, showAllApps = true)

/** Menu of a home apps list with "Show all apps" off: the rest of the apps stays hidden. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuLeftHandedScreenshotTest() =
    FavoritesMenuDialogCase(isLeftHanded = true, showAllApps = false)

@Composable
private fun FavoritesMainContentCase(isLeftHanded: Boolean, favoriteCount: Int, showAllApps: Boolean? = null) {
    CompositionLocalProvider(
        LocalHomeUiState provides createSampleHomeData().uiState.copy(isLeftHandedMode = isLeftHanded),
        LocalLeftHanded provides (createSampleHomeData().uiState.copy(isLeftHandedMode = isLeftHanded)).isLeftHandedMode
    ) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AureoleDS.colors.surface)
                    .padding(AureoleDS.dimens.medium)
            ) {
                FavoriteAppsMainContent(
                    config = FavoriteAppsDialogConfig(
                        allApps = emptyList(),
                        favoriteAppPackages = listOf("a", "b", "c", "d", "e").take(favoriteCount),
                        showAllApps = showAllApps?.let { ShowAllAppsParams(checked = it, onCheckedChange = {}) }
                    ),
                    onModeChange = {},
                    onCustomizeHome = {}
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMainContentRightHandedScreenshotTest() =
    FavoritesMainContentCase(isLeftHanded = false, favoriteCount = 5)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMainContentLeftHandedNoFavoritesScreenshotTest() =
    FavoritesMainContentCase(isLeftHanded = true, favoriteCount = 0)

/** A home apps list with "Show all apps" off and no favorites: the subtitle says none is selected. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMainContentShowAllOffNoFavoritesScreenshotTest() =
    FavoritesMainContentCase(isLeftHanded = false, favoriteCount = 0, showAllApps = false)

/** A home apps list with "Show all apps" on and no favorites: the Home shows all apps. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMainContentShowAllOnNoFavoritesScreenshotTest() =
    FavoritesMainContentCase(isLeftHanded = true, favoriteCount = 0, showAllApps = true)
