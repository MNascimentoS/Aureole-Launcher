package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppRow
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDragHint
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsEmptyBanner
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsSheetContent
import dev.mnascimentos.aureole.composable.favorites.RemainingAppRow
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.LocalReorderHandle
import dev.mnascimentos.aureole.composable.reorder.model.ReorderHandle
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val favoriteColors = listOf(0xFF2E7D32, 0xFFEF6C00, 0xFF1B65C0, 0xFF0288D1, 0xFF6A1B9A)

private val favoriteSampleApps = listOf("Telefone", "Mensagens", "Câmera", "Navegador", "Música").mapIndexed { i, label ->
    AppInfo(
        label = label,
        packageName = "fav.$i",
        componentName = ComponentName("fav.$i", "Main"),
        icon = favoriteColors[i].toInt().toDrawable()
    )
}

@Composable
private fun FavoritesManageCase(isLeftHanded: Boolean, favoriteCount: Int = 4) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .background(AureoleDS.colors.surface)
                    .padding(16.dp)
            ) {
                FavoriteAppsSheetContent(
                    currentMode = FavoriteAppsScreenMode.MANAGE,
                    config = FavoriteAppsDialogConfig(
                        allApps = favoriteSampleApps,
                        favoriteAppPackages = favoriteSampleApps.take(favoriteCount).map { it.packageName }
                    ),
                    actions = FavoriteAppsDialogActions(),
                    onModeChange = {}
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageListRightHandedScreenshotTest() = FavoritesManageCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageListLeftHandedScreenshotTest() = FavoritesManageCase(isLeftHanded = true)

/** No favorites: the banner says the Home shows all apps, no drag hint. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageNoFavoritesRightHandedScreenshotTest() =
    FavoritesManageCase(isLeftHanded = false, favoriteCount = 0)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageNoFavoritesLeftHandedScreenshotTest() =
    FavoritesManageCase(isLeftHanded = true, favoriteCount = 0)

/** One favorite: rows with the number, the ✕ and the handle, but no drag hint. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageOneFavoriteRightHandedScreenshotTest() =
    FavoritesManageCase(isLeftHanded = false, favoriteCount = 1)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManageOneFavoriteLeftHandedScreenshotTest() =
    FavoritesManageCase(isLeftHanded = true, favoriteCount = 1)

@Composable
private fun FavoritesManagePiecesCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AureoleDS.colors.surface)
                    .padding(AureoleDS.dimens.medium),
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
            ) {
                FavoriteAppsDragHint()
                FavoriteAppsEmptyBanner(appCount = 34)
                FavoriteAppRow(
                    app = favoriteSampleApps[0],
                    onRemove = {},
                    position = 1,
                    hasDragHandle = true
                )
                CompositionLocalProvider(LocalReorderHandle provides ReorderHandle(isDragging = true)) {
                    FavoriteAppRow(
                        app = favoriteSampleApps[1],
                        onRemove = {},
                        position = 2,
                        hasDragHandle = true
                    )
                }
                RemainingAppRow(app = favoriteSampleApps[2], onToggleFavorite = {})
            }
        }
    }
}

/** Drag hint, empty banner, a favorite row with the handle idle and held, and an other-app row with the +. */
@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManagePiecesRightHandedScreenshotTest() = FavoritesManagePiecesCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsManagePiecesLeftHandedScreenshotTest() = FavoritesManagePiecesCase(isLeftHanded = true)
