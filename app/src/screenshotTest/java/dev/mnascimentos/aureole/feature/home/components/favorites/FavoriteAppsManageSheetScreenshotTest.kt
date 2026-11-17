package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.ShowLabelsParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

@Composable
private fun ManageSheetCase(
    isLeftHanded: Boolean,
    favorites: List<String>,
    withSwitches: Boolean = false,
    fromHome: Boolean = false
) {
    val sampleData = createSampleHomeData()
    val config = FavoriteAppsDialogConfig(
        allApps = sampleData.apps,
        favoriteAppPackages = favorites,
        rowConfig = AppItemRowConfig(isLeftHandedMode = isLeftHanded),
        showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {}).takeIf { withSwitches },
        showLabels = ShowLabelsParams(checked = true, onCheckedChange = {}).takeIf { withSwitches }
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
            ProvideHomeUiState(sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)) {
                FavoriteAppsDialog(
                    config = config,
                    actions = if (fromHome) {
                        FavoriteAppsDialogActions(onDismiss = {}, onSettingsClick = {}, onCustomizeHome = {})
                    } else {
                        FavoriteAppsDialogActions(onDismiss = {})
                    }
                )
            }
        }
    }
}

private val sampleFavorites = listOf("com.example.phone", "com.example.camera")

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageSheetRightHandedScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = sampleFavorites)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageSheetLeftHandedScreenshotTest() =
    ManageSheetCase(isLeftHanded = true, favorites = sampleFavorites)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageSheetEmptyScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = emptyList())

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageSheetShowLabelsScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = sampleFavorites, withSwitches = true)

private val threeFavorites = listOf("com.example.phone", "com.example.messages", "com.example.camera")

/** From the home (favoritos-redesign): pencil and logo on the title, chips, sticky headers, 3 favorites. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsHomeSheetThreeFavoritesScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = threeFavorites, withSwitches = true, fromHome = true)

/** From the home without favorites: the one-line notice over the list. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsHomeSheetNoFavoritesScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = emptyList(), withSwitches = true, fromHome = true)

/** Left-handed: the logo goes to the start of the title line and the pencil to the end. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsHomeSheetLeftHandedScreenshotTest() =
    ManageSheetCase(isLeftHanded = true, favorites = threeFavorites, withSwitches = true, fromHome = true)

/** Landscape: preview on the left, sheet on the right, apps visible without scrolling (RF-05). */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun FavoriteAppsHomeSheetLandscapeThreeFavoritesScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = threeFavorites, withSwitches = true, fromHome = true)

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun FavoriteAppsHomeSheetLandscapeNoFavoritesScreenshotTest() =
    ManageSheetCase(isLeftHanded = false, favorites = emptyList(), withSwitches = true, fromHome = true)
