package dev.mnascimentos.aureole.telas.personalizar

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeBottomSheet
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/** Sheet "Personalizar" (oito tiles, sem logo no cabeçalho). */
@Composable
private fun CustomizeCase(isLeftHanded: Boolean) {
    val sampleData = createSampleHomeData()
    val uiState = sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)
    HomeCase(uiState) {
        CustomizeBottomSheet(uiState = uiState, actions = sampleData.actions, onDismissRequest = {})
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = CustomizeCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = CustomizeCase(isLeftHanded = true)
