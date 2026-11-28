/**
 * Favoritos.
 *
 * @en Favorites
 * @area home
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.favoritos

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.ShowLabelsParams
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private val THREE_FAVORITES = listOf("com.example.phone", "com.example.messages", "com.example.camera")

/** Favoritos aberto da home: lápis e logo no título, chips, cabeçalhos fixos e os switches. */
@Composable
private fun FavoritesCase(isLeftHanded: Boolean, favorites: List<String>) {
    val sampleData = createSampleHomeData()
    HomeCase(sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)) {
        FavoriteAppsDialog(
            config = FavoriteAppsDialogConfig(
                allApps = sampleData.apps,
                favoriteAppPackages = favorites,
                rowConfig = AppItemRowConfig(isLeftHandedMode = isLeftHanded),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {}),
                showLabels = ShowLabelsParams(checked = true, onCheckedChange = {})
            ),
            actions = FavoriteAppsDialogActions(onDismiss = {}, onSettingsClick = {})
        )
    }
}

/**
 * Padrão.
 *
 * @en Default
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = FavoritesCase(isLeftHanded = false, favorites = THREE_FAVORITES)

/**
 * Vazio.
 *
 * Sem favoritos: o aviso de uma linha sobre a lista.
 *
 * @en Empty
 */
@PreviewTest
@AureolePreview
@Composable
private fun VazioScreenshotTest() = FavoritesCase(isLeftHanded = false, favorites = emptyList())

/**
 * Canhoto.
 *
 * Canhoto: o logo vai para o início da linha do título e o lápis para o fim.
 *
 * @en Left-handed
 */
@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = FavoritesCase(isLeftHanded = true, favorites = THREE_FAVORITES)
