package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

@Composable
private fun ManageSheetCase(isLeftHanded: Boolean, favorites: List<String>) {
    val sampleData = createSampleHomeData()
    val config = FavoriteAppsDialogConfig(
        allApps = sampleData.apps,
        favoriteAppPackages = favorites,
        rowConfig = AppItemRowConfig(isLeftHandedMode = isLeftHanded)
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
            ProvideHomeUiState(sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)) {
                FavoriteAppsDialog(
                    config = config,
                    actions = FavoriteAppsDialogActions(onDismiss = {})
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
