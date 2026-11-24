package dev.mnascimentos.aureole.telas.ajustesopcao

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.ContainerPositionDialog

/** Sheet de opção única (posição do container); as de botão e de busca têm o mesmo layout. */
@Composable
private fun PositionCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            ContainerPositionDialog(currentPosition = "Center", onPositionSelected = {}, onDismiss = {})
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = PositionCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = PositionCase(isLeftHanded = true)
