package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
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

/** Single screen of a home apps list with "Show all apps" on: the switch says the favorites sit on top. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuRightHandedScreenshotTest() =
    FavoritesMenuDialogCase(isLeftHanded = false, showAllApps = true)

/** Single screen of a home apps list with "Show all apps" off: the rest of the apps stays hidden. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuLeftHandedScreenshotTest() =
    FavoritesMenuDialogCase(isLeftHanded = true, showAllApps = false)
