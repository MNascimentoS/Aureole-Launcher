/**
 * Seletor de favoritos.
 *
 * @en Favorites picker
 * @area home
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.favoritosseletor

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/**
 * Padrão.
 *
 * Seletor de favoritos de uma lista de apps da home, com "Mostrar todos os apps" ligado.
 *
 * @en Default
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() {
    val sampleData = createSampleHomeData()
    HomeCase(sampleData.uiState) {
        FavoriteAppsDialog(
            config = FavoriteAppsDialogConfig(
                allApps = sampleData.apps,
                favoriteAppPackages = sampleData.apps.map { it.packageName },
                rowConfig = AppItemRowConfig(isLeftHandedMode = false),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {})
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}
