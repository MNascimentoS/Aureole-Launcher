package dev.mnascimentos.aureole.telas.botaorapido

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonSettingsBottomSheet
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.fixtures.HomeCase

/** Menu do botão rápido: seção com atalho e aparência, Remover por último. */
@Composable
private fun QuickButtonSettingsCase(isLeftHanded: Boolean) {
    val uiState = MainUiState(activeQuickButtonModel = QuickButtonModel(), isLeftHandedMode = isLeftHanded)
    HomeCase(uiState) {
        QuickButtonSettingsBottomSheet(uiState = uiState, actions = createDefaultHomeScreenActions(), onDismissRequest = {})
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = QuickButtonSettingsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = QuickButtonSettingsCase(isLeftHanded = true)
