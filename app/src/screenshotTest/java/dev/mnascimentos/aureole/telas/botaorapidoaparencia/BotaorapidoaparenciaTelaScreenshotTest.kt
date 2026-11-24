package dev.mnascimentos.aureole.telas.botaorapidoaparencia

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonAppearanceBottomSheet
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.fixtures.HomeCase

/** Editor de aparência: prévia ancorada, Cancelar / Salvar com Salvar sob o polegar. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() {
    val uiState = MainUiState(activeQuickButtonModel = QuickButtonModel())
    HomeCase(uiState) {
        QuickButtonAppearanceBottomSheet(uiState = uiState, actions = createDefaultHomeScreenActions(), onDismissRequest = {})
    }
}
