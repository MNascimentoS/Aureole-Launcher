package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsSheetContent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
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
private fun FavoritesManageCase(isLeftHanded: Boolean) {
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
                    config = FavoriteAppsDialogConfig(
                        allApps = favoriteSampleApps,
                        favoriteAppPackages = favoriteSampleApps.take(4).map { it.packageName }
                    ),
                    actions = FavoriteAppsDialogActions()
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
