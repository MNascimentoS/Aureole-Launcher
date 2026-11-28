/**
 * Menu do app.
 *
 * @en App menu
 * @area home
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.menuapp

import android.content.pm.ShortcutInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData
import dev.mnascimentos.aureole.fixtures.sampleApp
import dev.mnascimentos.aureole.util.AureoleShortcutItem

@Composable
private fun sampleShortcuts(): List<AureoleShortcutItem> {
    val context = LocalContext.current
    return listOf("Ride" to "ride", "Delivery" to "delivery").map { (label, id) ->
        AureoleShortcutItem(
            id = id,
            label = label,
            icon = null,
            shortcutInfo = ShortcutInfo.Builder(context, id).setShortLabel(label).build()
        )
    }
}

/** Menu de um app da home com atalhos do app, favorito e "Personalizar"; Remover por último. */
@Composable
private fun AppMenuCase(isLeftHanded: Boolean) =
    HomeCase(createSampleHomeData().uiState.copy(isLeftHandedMode = isLeftHanded)) {
        AppItemBottomSheet(
            params = AppItemBottomSheetParams(
                app = sampleApp("Browser", "com.example.browser", 0xFF1D8FE0),
                isFavorite = true,
                actions = AppItemRowActions(onToggleFavorite = {}, onCustomizeHome = {}),
                onOpenContainerSettings = {},
                shortcuts = sampleShortcuts()
            ),
            onDismiss = {}
        )
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
private fun PadraoScreenshotTest() = AppMenuCase(isLeftHanded = false)

/**
 * Canhoto.
 *
 * @en Left-handed
 */
@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = AppMenuCase(isLeftHanded = true)
