package dev.mnascimentos.aureole.telas.atalhos

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.ShortcutSelectionBottomSheet
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionOptions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.sampleApp

private val SHORTCUT_APPS = listOf(
    sampleApp("Camera Pro", "com.pro", 0xFF1B65C0),
    sampleApp("Camcorder", "com.camcorder", 0xFF2E7D32),
    sampleApp("Maps", "com.maps", 0xFFC62828)
)

/** Seletor de atalho do toque do botão rápido; com [query], a busca já aberta. */
@Composable
private fun ShortcutCase(query: String = "") {
    val uiState = MainUiState(
        activeQuickButtonModel = QuickButtonModel(shortcuts = QuickButtonShortcuts(click = ShortcutAction.AllApps)),
        editingShortcutType = "click",
        apps = SHORTCUT_APPS
    )
    HomeCase(uiState) {
        ShortcutSelectionBottomSheet(
            uiState = uiState,
            actions = createDefaultHomeScreenActions(),
            onDismissRequest = {},
            options = ShortcutSelectionOptions(initialSearchQuery = query)
        )
    }
}

@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = ShortcutCase()

@PreviewTest
@AureolePreview
@Composable
private fun BuscaScreenshotTest() = ShortcutCase(query = "cam")
