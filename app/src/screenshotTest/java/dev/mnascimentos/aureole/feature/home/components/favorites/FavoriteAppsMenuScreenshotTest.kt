package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsMainContent
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsSheetContent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

@Composable
private fun FavoritesMenuDialogCase(isLeftHanded: Boolean) {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)
        ) {
            FavoriteAppsDialog(
                config = FavoriteAppsDialogConfig(
                    allApps = sampleData.apps,
                    favoriteAppPackages = sampleData.apps.take(5).map { it.packageName },
                    rowConfig = AppItemRowConfig(isLeftHandedMode = isLeftHanded)
                ),
                actions = FavoriteAppsDialogActions()
            )
        }
    }
}

/** Favorites main menu: two rows and the full-width Done button. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuRightHandedScreenshotTest() = FavoritesMenuDialogCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsMenuLeftHandedScreenshotTest() = FavoritesMenuDialogCase(isLeftHanded = true)

@Composable
private fun FavoritesMainContentCase(isLeftHanded: Boolean, favoriteCount: Int) {
    CompositionLocalProvider(
        LocalHomeUiState provides createSampleHomeData().uiState.copy(isLeftHandedMode = isLeftHanded)
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
                        favoriteAppPackages = listOf("a", "b", "c", "d", "e").take(favoriteCount)
                    ),
                    onModeChange = {}
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

@Composable
private fun FavoritesSubScreenCase(mode: FavoriteAppsScreenMode, isLeftHanded: Boolean) {
    CompositionLocalProvider(
        LocalHomeUiState provides createSampleHomeData().uiState.copy(isLeftHandedMode = isLeftHanded)
    ) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AureoleDS.colors.surface)
                    .padding(AureoleDS.dimens.medium)
            ) {
                FavoriteAppsSheetContent(
                    currentMode = mode,
                    config = FavoriteAppsDialogConfig(
                        allApps = createSampleHomeData().apps,
                        favoriteAppPackages = createSampleHomeData().apps.take(3).map { it.packageName }
                    ),
                    actions = FavoriteAppsDialogActions(),
                    onModeChange = {}
                )
            }
        }
    }
}

/** The "Edit shortcuts" sub-screen opened from the menu: the shortcut list. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsEditShortcutsSubScreenScreenshotTest() =
    FavoritesSubScreenCase(mode = FavoriteAppsScreenMode.EDIT_SHORTCUTS, isLeftHanded = true)
